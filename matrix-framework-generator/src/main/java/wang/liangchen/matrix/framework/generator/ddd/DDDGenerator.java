package wang.liangchen.matrix.framework.generator.ddd;


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
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.network.URIUtil;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.data.dao.criteria.ColumnMeta;
import wang.liangchen.matrix.framework.data.datasource.ConnectionManager;
import wang.liangchen.matrix.framework.ddd.southbound_acl.port.PortType;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.domain.EntityProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.SouthboundAclProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.adapter.AdapterProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.message_pl.SouthboundMessagePlProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.port.PortProperties;
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
public class DDDGenerator {
    private final StandaloneDao standaloneDao;
    private final static String SQL = "select * from %s where 1=0";
    private final static String JAVA = ".java";
    private final static String GENERATOR_CONFIG_FILE = "ddd-generator.xml";
    private final Configuration freemarkerConfig;

    public DDDGenerator(StandaloneDao standaloneDao) {
        this.standaloneDao = standaloneDao;
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
        this.freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/templates/ddd");
        String encoding = StandardCharsets.UTF_8.name();
        this.freemarkerConfig.setDefaultEncoding(encoding);
        this.freemarkerConfig.setOutputEncoding(encoding);
        this.freemarkerConfig.setEncoding(Locale.getDefault(), encoding);
        this.freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public void generate() {
        // 获取配置
        List<EntityProperties> entitiesProperties = resolveConfiguration();
        // 校验聚合根唯一
        Set<String> aggregates = new HashSet<>();

        // 填充数据库信息
        for (EntityProperties entityProperties : entitiesProperties) {
            if (entityProperties.getAggregateRoot()) {
                String aggregateName = entityProperties.getAggregateName();
                if (aggregates.contains(aggregateName)) {
                    throw new MatrixInfoException("A aggregate has one aggregate only:{}", aggregateName);
                } else {
                    aggregates.add(aggregateName);
                }
            }

            createBoundedContextPackageInfo(entityProperties);
            createDomainPackageInfo(entityProperties);
            createAggregatePackageInfo(entityProperties);
            createEntity(entityProperties);

            SouthboundAclProperties southboundAclProperties = createSouthboundAclPackageInfo(entityProperties);
            createSouthboundAclMessagePlPackageInfo(entityProperties, southboundAclProperties);
            PortProperties portProperties = createPortPackageInfo(entityProperties, southboundAclProperties);
            List<PortProperties> portsProperties = createPort(entityProperties, portProperties);
            AdapterProperties adapterProperties = createAdapterPackageInfo(entityProperties, southboundAclProperties);
            createAdapter(entityProperties, adapterProperties, portsProperties);
        }
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

        Element boundedContext = document.getDocumentElement();
        String boundedContextName = boundedContext.getAttribute("name");
        ValidationUtil.INSTANCE.notBlank(boundedContextName, "The 'name' attribute of the bounded context must not be blank");
        String basePackage = boundedContext.getAttribute("package");
        ValidationUtil.INSTANCE.notBlank(basePackage, "The 'package' attribute of the bounded context must not be blank");
        String datasource = boundedContext.getAttribute("datasource");
        String author = boundedContext.getAttribute("author");
        String output = boundedContext.getAttribute("output");
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
        NodeList aggregates = boundedContext.getElementsByTagName("aggregate");
        for (int i = 0, j = aggregates.getLength(); i < j; i++) {
            Element aggregateElement = (Element) aggregates.item(i);
            String aggregateName = aggregateElement.getAttribute("name");
            ValidationUtil.INSTANCE.notBlank(aggregateName, "The 'name' attribute of the aggregate must not be blank");

            NodeList entities = aggregateElement.getElementsByTagName("entity");
            for (int k = 0, l = entities.getLength(); k < l; k++) {
                EntityProperties entityProperties = new EntityProperties();
                // 填充限界属性
                entityProperties.setBoundedContextName(boundedContextName);
                entityProperties.setBasePackage(basePackage);
                entityProperties.setBoundedContextPackage(basePackage.concat(Symbol.DOT.getSymbol()).concat(boundedContextName));
                entityProperties.setDatasource(datasource);
                entityProperties.setAuthor(author);
                entityProperties.setOutput(output);
                // 填充领域属性
                entityProperties.setDomainPackage(entityProperties.getBoundedContextPackage().concat(Symbol.DOT.getSymbol()).concat("domain"));
                // 填充聚合属性
                entityProperties.setAggregateName(aggregateName);
                entityProperties.setAggregatePackage(entityProperties.getDomainPackage().concat(Symbol.DOT.getSymbol()).concat(aggregateName));
                // 填充自身属性
                Element entityElement = (Element) entities.item(k);
                String tableName = entityElement.getAttribute("table-name");
                ValidationUtil.INSTANCE.notBlank(tableName, "The 'table-name' attribute of the entity must not be blank");
                entityProperties.setTableName(tableName);
                String entityName = entityElement.getAttribute("name");
                entityName = StringUtil.INSTANCE.isBlank(entityName) ? StringUtil.INSTANCE.underline2UpperCamelCase(tableName) : entityName;
                entityProperties.setEntityName(entityName);
                String aggregateRoot = entityElement.getAttribute("aggregate-root");
                Boolean isAggregateRoot = ObjectUtil.INSTANCE.castToBoolean(aggregateRoot, false);
                entityProperties.setAggregateRoot(isAggregateRoot);

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
                entityProperties.setEntityPackage(entityProperties.getAggregatePackage());
                entitiesProperties.add(entityProperties);
            }
        }
        return entitiesProperties;
    }

    private void createBoundedContextPackageInfo(EntityProperties entityProperties) {
        createFile(entityProperties.getOutput(), entityProperties.getBoundedContextPackage(), "package-info.java", "BoundedContextPackageInfo.ftl", entityProperties);
    }

    private void createDomainPackageInfo(EntityProperties entityProperties) {
        createFile(entityProperties.getOutput(), entityProperties.getDomainPackage(), "package-info.java", "DomainPackageInfo.ftl", entityProperties);
    }

    private void createAggregatePackageInfo(EntityProperties entityProperties) {
        createFile(entityProperties.getOutput(), entityProperties.getAggregatePackage(), "package-info.java", "AggregatePackageInfo.ftl", entityProperties);
    }

    private void createEntity(EntityProperties entityProperties) {
        // 填充数据元信息
        populateColumnMetas(entityProperties);
        createFile(entityProperties.getOutput(), entityProperties.getEntityPackage(), entityProperties.getEntityName().concat(".java"), "Entity.ftl", entityProperties);
    }

    private SouthboundAclProperties createSouthboundAclPackageInfo(EntityProperties entityProperties) {
        SouthboundAclProperties southboundAclProperties = new SouthboundAclProperties();
        southboundAclProperties.setSouthboundAclPackage(entityProperties.getBoundedContextPackage().concat(Symbol.DOT.getSymbol()).concat("southbound_acl"));
        southboundAclProperties.setAuthor(entityProperties.getAuthor());
        createFile(entityProperties.getOutput(), southboundAclProperties.getSouthboundAclPackage(), "package-info.java", "SouthBoundAclPackageInfo.ftl", southboundAclProperties);
        return southboundAclProperties;
    }

    private SouthboundMessagePlProperties createSouthboundAclMessagePlPackageInfo(EntityProperties entityProperties, SouthboundAclProperties southboundAclProperties) {
        SouthboundMessagePlProperties southboundMessagePlProperties = new SouthboundMessagePlProperties();
        // 填充南向网关
        southboundMessagePlProperties.setSouthboundAclPackage(southboundAclProperties.getSouthboundAclPackage());
        southboundMessagePlProperties.setAuthor(entityProperties.getAuthor());
        // 填充
        southboundMessagePlProperties.setMessagePlPackage(southboundMessagePlProperties.getSouthboundAclPackage().concat(Symbol.DOT.getSymbol()).concat("message_pl"));
        createFile(entityProperties.getOutput(), southboundMessagePlProperties.getMessagePlPackage(), "package-info.java", "SouthboundMessagePlPackageInfo.ftl", southboundMessagePlProperties);
        return southboundMessagePlProperties;
    }

    private PortProperties createPortPackageInfo(EntityProperties entityProperties, SouthboundAclProperties southboundAclProperties) {
        String portPackage = southboundAclProperties.getSouthboundAclPackage().concat(Symbol.DOT.getSymbol()).concat("port");
        PortProperties portProperties = new PortProperties();
        portProperties.setPortPackage(portPackage);
        createFile(entityProperties.getOutput(), portProperties.getPortPackage(), "package-info.java", "PortPackageInfo.ftl", portProperties);
        return portProperties;
    }

    private List<PortProperties> createPort(EntityProperties entityProperties, PortProperties portProperties) {
        if (!entityProperties.getAggregateRoot()) {
            return Collections.EMPTY_LIST;
        }
        List<PortProperties> portPropertiesList = new ArrayList<>();
        String portPackage = portProperties.getPortPackage();
        for (PortType portType : PortType.values()) {
            String portTypeName = portType.name();
            portProperties = new PortProperties();
            portProperties.setAuthor(entityProperties.getAuthor());
            portProperties.setPortPackage(portPackage.concat(Symbol.DOT.getSymbol()).concat(portTypeName.toLowerCase()));
            portProperties.setPortType(portType);
            portProperties.setPortClassName(entityProperties.getEntityName().concat(portTypeName));
            createFile(entityProperties.getOutput(), portProperties.getPortPackage(), portProperties.getPortClassName().concat(".java"), portTypeName.concat("PortInterface.ftl"), portProperties);
            portPropertiesList.add(portProperties);
        }
        return portPropertiesList;
    }

    private AdapterProperties createAdapterPackageInfo(EntityProperties entityProperties, SouthboundAclProperties southboundAclProperties) {
        String adapterPackage = southboundAclProperties.getSouthboundAclPackage().concat(Symbol.DOT.getSymbol()).concat("adapter");
        AdapterProperties adapterProperties = new AdapterProperties();
        adapterProperties.setAdapterPackage(adapterPackage);
        createFile(entityProperties.getOutput(), adapterProperties.getAdapterPackage(), "package-info.java", "AdapterPackageInfo.ftl", adapterProperties);
        return adapterProperties;
    }

    private void createAdapter(EntityProperties entityProperties, AdapterProperties adapterProperties, List<PortProperties> portsProperties) {
        if (!entityProperties.getAggregateRoot()) {
            return;
        }
        String adapterPackage = adapterProperties.getAdapterPackage();
        for (PortProperties portProperties : portsProperties) {
            PortType portType = portProperties.getPortType();
            String portTypeName = portType.name();
            adapterProperties = new AdapterProperties();
            adapterProperties.setAuthor(entityProperties.getAuthor());
            adapterProperties.setPortPackage(portProperties.getPortPackage());
            adapterProperties.setPortClassName(portProperties.getPortClassName());
            adapterProperties.setAdapterPackage(adapterPackage.concat(Symbol.DOT.getSymbol()).concat(portTypeName.toLowerCase()));
            adapterProperties.setPortType(portType);
            adapterProperties.setAdapterClassName(entityProperties.getEntityName().concat(portTypeName).concat("Impl"));
            createFile(entityProperties.getOutput(), adapterProperties.getAdapterPackage(), adapterProperties.getAdapterClassName().concat(".java"), portTypeName.concat("AdapterImpl.ftl"), adapterProperties);
        }
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

