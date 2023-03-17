package wang.liangchen.matrix.framework.generator.tier3;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.network.URIUtil;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.dao.criteria.ColumnMeta;
import wang.liangchen.matrix.framework.data.datasource.ConnectionManager;
import wang.liangchen.matrix.framework.generator.tier3.dao.EntityProperties;
import wang.liangchen.matrix.framework.springboot.env.EnvironmentContext;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang 2022-12-28 18:31
 */
public class Tier3Generator {
    private final static String SQL = "select * from %s where 1=0";
    private final static String JAVA = ".java";
    private final static String GENERATOR_CONFIG_FILE = "tier3-generator.xml";
    private final Configuration freemarkerConfig;

    public Tier3Generator() {
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
        this.freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/templates/tier3");
        String encoding = StandardCharsets.UTF_8.name();
        this.freemarkerConfig.setDefaultEncoding(encoding);
        this.freemarkerConfig.setOutputEncoding(encoding);
        this.freemarkerConfig.setEncoding(Locale.getDefault(), encoding);
        this.freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public void generate() {
        // 获取配置
        List<EntityProperties> entitiesProperties = resolveConfiguration();
        // 填充数据库信息
        for (EntityProperties entityProperties : entitiesProperties) {
            createEntity(entityProperties);
            createService(entityProperties);
            createRequest(entityProperties);
            createResponse(entityProperties);
            createController(entityProperties);
        }
    }

    private void createEntity(EntityProperties entityProperties) {
        populateColumnMetas(entityProperties);
        createFile(entityProperties.getOutput(), entityProperties.getEntityPackage(), entityProperties.getEntityName().concat(".java"), "Entity.ftl", entityProperties);
    }

    private void createService(EntityProperties entityProperties) {
        // 补充service信息
        entityProperties.setServicePackage(entityProperties.getBasePackage().concat(Symbol.DOT.getSymbol()).concat("service"));
        entityProperties.setServiceName("I".concat(entityProperties.getEntityName()).concat("Service"));
        if (entityProperties.getIgnoreInterface()) {
            entityProperties.setServiceImplPackage(entityProperties.getServicePackage());
            entityProperties.setServiceImplName(entityProperties.getEntityName().concat("Service"));
        } else {
            entityProperties.setServiceImplPackage(entityProperties.getServicePackage().concat(Symbol.DOT.getSymbol()).concat("impl"));
            entityProperties.setServiceImplName(entityProperties.getEntityName().concat("ServiceImpl"));
        }
        // Request
        entityProperties.setRequestPackage(entityProperties.getServicePackage().concat(Symbol.DOT.getSymbol()).concat("message_pl"));
        entityProperties.setRequestName(entityProperties.getEntityName().concat("Request"));
        // Response
        entityProperties.setResponsePackage(entityProperties.getServicePackage().concat(Symbol.DOT.getSymbol()).concat("message_pl"));
        entityProperties.setResponseName(entityProperties.getEntityName().concat("Response"));

        if (entityProperties.getIgnoreInterface()) {
            // create impl
            createFile(entityProperties.getOutput(), entityProperties.getServiceImplPackage(), entityProperties.getServiceImplName().concat(".java"), "ServiceImplWithoutInterface.ftl", entityProperties);
        } else {
            // create interface
            createFile(entityProperties.getOutput(), entityProperties.getServicePackage(), entityProperties.getServiceName().concat(".java"), "Service.ftl", entityProperties);
            // create impl
            createFile(entityProperties.getOutput(), entityProperties.getServiceImplPackage(), entityProperties.getServiceImplName().concat(".java"), "ServiceImpl.ftl", entityProperties);
        }
    }

    private void createRequest(EntityProperties entityProperties) {
        createFile(entityProperties.getOutput(), entityProperties.getRequestPackage(), entityProperties.getRequestName().concat(".java"), "Request.ftl", entityProperties);
    }

    private void createResponse(EntityProperties entityProperties) {
        createFile(entityProperties.getOutput(), entityProperties.getResponsePackage(), entityProperties.getResponseName().concat(".java"), "Response.ftl", entityProperties);
    }

    private void createController(EntityProperties entityProperties) {
        // 补充controller信息
        entityProperties.setControllerPackage(entityProperties.getBasePackage().concat(Symbol.DOT.getSymbol()).concat("controller"));
        entityProperties.setControllerName(entityProperties.getEntityName().concat("Controller"));
        String templateName="Controller.ftl";
        if(entityProperties.getIgnoreInterface()){
            templateName="ControllerWithoutInterface.ftl";
        }
        createFile(entityProperties.getOutput(), entityProperties.getControllerPackage(), entityProperties.getControllerName().concat(".java"), templateName, entityProperties);
    }

    private List<EntityProperties> resolveConfiguration() {
        String location = EnvironmentContext.INSTANCE.getURI(GENERATOR_CONFIG_FILE).toString();
        Document document;
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = documentBuilder.parse(location);
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }

        Element rootElement = document.getDocumentElement();
        String basePackage = rootElement.getAttribute("package");
        String datasource = rootElement.getAttribute("datasource");
        String author = rootElement.getAttribute("author");
        String output = rootElement.getAttribute("output");
        if (StringUtil.INSTANCE.isBlank(output)) {
            try {
                output = ResourceUtils.getURL("").toString();
                output = output.concat("src").concat(Symbol.URI_SEPARATOR.getSymbol())
                        .concat("main").concat(Symbol.URI_SEPARATOR.getSymbol())
                        .concat("java").concat(Symbol.URI_SEPARATOR.getSymbol());
            } catch (FileNotFoundException e) {
                throw new MatrixErrorException(e);
            }
        }

        List<EntityProperties> entitiesProperties = new ArrayList<>();
        NodeList entities = rootElement.getElementsByTagName("entity");
        for (int k = 0, l = entities.getLength(); k < l; k++) {
            EntityProperties entityProperties = new EntityProperties();
            // 填充根属性
            entityProperties.setBasePackage(basePackage);
            entityProperties.setDatasource(datasource);
            entityProperties.setAuthor(author);
            entityProperties.setOutput(output);
            entityProperties.setIgnoreInterface(Boolean.TRUE);
            // 填充自身属性
            Element entityElement = (Element) entities.item(k);
            String tableName = entityElement.getAttribute("table-name");
            ValidationUtil.INSTANCE.notBlank(tableName, "The 'table-name' attribute of the entity must not be blank");
            entityProperties.setTableName(tableName);
            String entityName = entityElement.getAttribute("name");
            entityName = StringUtil.INSTANCE.isBlank(entityName) ? StringUtil.INSTANCE.underline2UpperCamelCase(tableName) : entityName;
            entityProperties.setEntityName(entityName);

            String columnVersion = null;
            NodeList nodes = entityElement.getElementsByTagName("column-version");
            Element element;
            if (nodes.getLength() == 1) {
                element = (Element) nodes.item(0);
                columnVersion = element.getTextContent();
            }
            entityProperties.setColumnVersion(columnVersion);
            String columnJson = null;
            nodes = entityElement.getElementsByTagName("column-json");
            if (nodes.getLength() == 1) {
                element = (Element) nodes.item(0);
                columnJson = element.getTextContent();
            }
            entityProperties.setColumnJson(columnJson);
            String columnState = null;
            Boolean isUseConstantEnum = null;
            nodes = entityElement.getElementsByTagName("column-state");
            if (nodes.getLength() == 1) {
                element = (Element) nodes.item(0);
                columnState = element.getTextContent();
                String useConstantEnum = element.getAttribute("useConstantEnum");
                isUseConstantEnum = ObjectUtil.INSTANCE.castToBoolean(useConstantEnum);
            }
            entityProperties.setColumnState(columnState);
            entityProperties.setColumnStateUseConstantEnum(isUseConstantEnum);

            String columnMarkDelete = null;
            String columnMarkDeleteValue = null;
            nodes = entityElement.getElementsByTagName("column-markdelete");
            if (nodes.getLength() == 1) {
                element = (Element) nodes.item(0);
                columnMarkDelete = element.getTextContent();
                columnMarkDeleteValue = element.getAttribute("value");
            }
            entityProperties.setColumnMarkDelete(columnMarkDelete);
            entityProperties.setColumnMarkDeleteValue(columnMarkDeleteValue);
            entityProperties.setEntityPackage(entityProperties.getBasePackage().concat(Symbol.DOT.getSymbol()).concat("dao")
                    .concat(Symbol.DOT.getSymbol()).concat("entity"));
            entitiesProperties.add(entityProperties);
        }
        return entitiesProperties;
    }

    private void createFile(String output, String packageName, String fileName, String templateName, Object dataModel) {
        URI uri = URIUtil.INSTANCE.toURI(output, StringUtil.INSTANCE.package2Path(packageName));
        Path path = Paths.get(uri);
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path);
            }
            Path filePath = path.resolve(fileName);
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                Template template = freemarkerConfig.getTemplate(templateName);
                template.process(dataModel, new OutputStreamWriter(Files.newOutputStream(filePath), StandardCharsets.UTF_8));
            }
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private void populateColumnMetas(EntityProperties entityProperties) {
        String tableName = entityProperties.getTableName();
        ConnectionManager.INSTANCE.executeInNonManagedConnection((connection) -> {
            try {
                // 查询主键唯一键注释等信息
                DatabaseMetaData databaseMetaData = connection.getMetaData();
                ResultSet tables = databaseMetaData.getTables(null, null, tableName, new String[]{"TABLE"});
                while (tables.next()) {
                    String tableRemarks = tables.getString("REMARKS");
                    entityProperties.setTableComment(tableRemarks);
                }
                tables.close();
                ResultSet columns = databaseMetaData.getColumns(null, null, tableName, null);
                Map<String, String> columnCommentMap = new HashMap<>();
                while (columns.next()) {
                    columnCommentMap.put(columns.getString("COLUMN_NAME"), columns.getString("REMARKS"));
                }
                columns.close();

                List<String> primaryKeyColumnNames = primaryKeyColumnNames(databaseMetaData, tableName);
                List<String> uniqueKeyColumnNames = uniqueKeyColumnNames(databaseMetaData, tableName);
                uniqueKeyColumnNames.removeAll(primaryKeyColumnNames);

                List<ColumnMeta> columnMetas = resolveResultSetMetaData(connection, tableName, entityProperties, primaryKeyColumnNames, uniqueKeyColumnNames, columnCommentMap);
                entityProperties.getColumnMetas().addAll(columnMetas);
                entityProperties.getPkColumnMetas().addAll(columnMetas.stream().filter(ColumnMeta::isId).collect(Collectors.toList()));
                columnMetas.stream().filter(ColumnMeta::isState)
                        .findFirst().ifPresent(entityProperties::setStateColumnMeta);

                // 构造imports
                Set<String> imports = columnMetas.stream().map(ColumnMeta::getImportPackage).filter(StringUtil.INSTANCE::isNotBlank).collect(Collectors.toSet());
                entityProperties.getImports().addAll(imports);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    private List<String> primaryKeyColumnNames(DatabaseMetaData databaseMetaData, String tableName) throws SQLException {
        List<String> primaryKeyColumnNames = new ArrayList<>();
        ResultSet resultSet = databaseMetaData.getPrimaryKeys(null, null, tableName);
        while (resultSet.next()) {
            String columnName = resultSet.getString("COLUMN_NAME");
            primaryKeyColumnNames.add(columnName);
        }
        resultSet.close();
        return primaryKeyColumnNames;

    }

    private List<String> uniqueKeyColumnNames(DatabaseMetaData databaseMetaData, String tableName) throws SQLException {
        List<String> uniqueKeyColumnNames = new ArrayList<>();
        ResultSet resultSet = databaseMetaData.getIndexInfo(null, null, tableName, true, false);
        while (resultSet.next()) {
            String columnName = resultSet.getString("COLUMN_NAME");
            uniqueKeyColumnNames.add(columnName);
        }
        resultSet.close();
        return uniqueKeyColumnNames;

    }

    private List<ColumnMeta> resolveResultSetMetaData(Connection connection, String tableName, EntityProperties entityProperties, List<String> primaryKeyColumnNames, List<String> uniqueKeyColumnNames, Map<String, String> columnCommentMap) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL, tableName));
        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        String columnName, dataTypeName, jdbcTypeName;
        ColumnMeta columnMeta;
        List<ColumnMeta> columnMetas = new ArrayList<>();
        for (int i = 1, j = resultSetMetaData.getColumnCount(); i <= j; i++) {
            columnName = resultSetMetaData.getColumnName(i);
            dataTypeName = resultSetMetaData.getColumnTypeName(i);
            jdbcTypeName = resultSetMetaData.getColumnClassName(i);

            boolean isId = primaryKeyColumnNames.contains(columnName);
            boolean isUnique = uniqueKeyColumnNames.contains(columnName);
            boolean isVersion = columnName.equals(entityProperties.getColumnVersion());
            boolean isJson = columnName.equals(entityProperties.getColumnJson());
            boolean isState = columnName.equals(entityProperties.getColumnState());
            boolean isStateUseConstantEnum = entityProperties.isColumnStateUseConstantEnum();
            String _deleteValue = columnName.equals(entityProperties.getColumnMarkDelete()) ? entityProperties.getColumnMarkDeleteValue() : null;
            String columnComment = columnCommentMap.getOrDefault(columnName, "");
            columnComment = null == columnComment ? "" : columnComment;
            columnMeta = ColumnMeta.newInstance(columnName, dataTypeName, jdbcTypeName, isId, isUnique, isVersion, isJson, isState, isStateUseConstantEnum, _deleteValue, true, columnComment);
            columnMetas.add(columnMeta);
        }
        preparedStatement.close();
        resultSet.close();
        return columnMetas;
    }
}

