package wang.liangchen.matrix.framework.generator;


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
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.data.dao.criteria.ColumnMeta;
import wang.liangchen.matrix.framework.data.datasource.ConnectionManager;
import wang.liangchen.matrix.framework.springboot.env.EnvironmentContext;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
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
public class DDDStyleGenerator {
    private final StandaloneDao standaloneDao;
    private final static String SQL = "select * from %s where 1=0";
    private final static String JAVA = ".java";
    private final static String GENERATOR_CONFIG_FILE = "ddd-generator.xml";
    private final Configuration freemarkerConfig;

    public DDDStyleGenerator(StandaloneDao standaloneDao) {
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
        // 获取配置信息
        BoundedContextProperties boundedContextProperties = resolveConfiguration();
        createBoundedContext(boundedContextProperties);
        createDomain(boundedContextProperties);
        createAggregates(boundedContextProperties);
        createNorthboundOhs(boundedContextProperties);
        createSouthboundAcl(boundedContextProperties);
    }


    private BoundedContextProperties resolveConfiguration() {
        String location = EnvironmentContext.INSTANCE.getURI(GENERATOR_CONFIG_FILE).toString();
        Document document;
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = documentBuilder.parse(location);
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }

        BoundedContextProperties boundedContextProperties = new BoundedContextProperties();
        Element boundedContext = document.getDocumentElement();
        String boundedContextName = boundedContext.getAttribute("name");
        ValidationUtil.INSTANCE.notBlank(boundedContextName, "The 'name' attribute of the bounded context must not be blank");
        boundedContextProperties.setBoundedContextName(boundedContextName);
        String basePackage = boundedContext.getAttribute("package");
        ValidationUtil.INSTANCE.notBlank(boundedContextName, "The 'package' attribute of the bounded context must not be blank");
        boundedContextProperties.setBasePackage(basePackage);
        boundedContextProperties.setBoundedContextPackage(basePackage.concat(Symbol.DOT.getSymbol()).concat(boundedContextName));
        String datasource = boundedContext.getAttribute("datasource");
        boundedContextProperties.setDatasource(datasource);
        String author = boundedContext.getAttribute("author");
        boundedContextProperties.setAuthor(author);
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
        boundedContextProperties.setOutput(output);

        DomainProperties domainProperties = new DomainProperties();
        domainProperties.setDomainPackage(boundedContextProperties.getBoundedContextPackage().concat(Symbol.DOT.getSymbol()).concat("domain"));
        boundedContextProperties.setDomainProperties(domainProperties);

        NorthboundOhsProperties northboundOhsProperties = new NorthboundOhsProperties();
        northboundOhsProperties.setNorthboundOhsPackage(boundedContextProperties.getBoundedContextPackage().concat(Symbol.DOT.getSymbol()).concat("northbound_ohs"));
        boundedContextProperties.setNorthboundOhsProperties(northboundOhsProperties);

        SouthboundAclProperties southboundAclProperties = new SouthboundAclProperties();
        southboundAclProperties.setSouthboundAclPackage(boundedContextProperties.getBoundedContextPackage().concat(Symbol.DOT.getSymbol()).concat("southbound_acl"));
        boundedContextProperties.setSouthboundAclProperties(southboundAclProperties);

        NodeList aggregates = boundedContext.getElementsByTagName("aggregate");
        for (int i = 0, j = aggregates.getLength(); i < j; i++) {
            AggregateProperties aggregateProperties = new AggregateProperties();
            Element aggregateElement = (Element) aggregates.item(i);
            String aggregateName = aggregateElement.getAttribute("name");
            ValidationUtil.INSTANCE.notBlank(aggregateName, "The 'name' attribute of the aggregate must not be blank");
            aggregateProperties.setAggregateName(aggregateName);
            aggregateProperties.setAggregatePackage(domainProperties.getDomainPackage()
                    .concat(Symbol.DOT.getSymbol()).concat(aggregateName));

            NodeList entities = aggregateElement.getElementsByTagName("entity");
            for (int k = 0, l = entities.getLength(); k < l; k++) {
                EntityProperties entityProperties = new EntityProperties();
                Element entityElement = (Element) entities.item(k);
                String tableName = entityElement.getAttribute("table-name");
                ValidationUtil.INSTANCE.notBlank(boundedContextName, "The 'table-name' attribute of the entity must not be blank");
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
                entityProperties.setEntityPackage(aggregateProperties.getAggregatePackage());
                entityProperties.setAuthor(author);
                aggregateProperties.addEntityProperties(entityProperties);
            }
            boundedContextProperties.addAggregateProperties(aggregateProperties);
        }
        return boundedContextProperties;
    }

    private void createBoundedContext(BoundedContextProperties boundedContextProperties) {
        URI uri = URIUtil.INSTANCE.toURI(boundedContextProperties.getOutput(), StringUtil.INSTANCE.package2Path(boundedContextProperties.getBoundedContextPackage()));
        Path path = Paths.get(uri);
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path);
            }
            Path packageInfoFilePath = path.resolve("package-info.java");
            if (Files.notExists(packageInfoFilePath)) {
                Files.createFile(packageInfoFilePath);
                Template template = freemarkerConfig.getTemplate("BoundedContext.ftl");
                template.process(boundedContextProperties, new OutputStreamWriter(Files.newOutputStream(packageInfoFilePath), StandardCharsets.UTF_8));
            }
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private void createDomain(BoundedContextProperties boundedContextProperties) {
        DomainProperties domainProperties = boundedContextProperties.getDomainProperties();
        URI uri = URIUtil.INSTANCE.toURI(boundedContextProperties.getOutput(), StringUtil.INSTANCE.package2Path(domainProperties.getDomainPackage()));
        Path path = Paths.get(uri);
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path);
            }
            Path packageInfoFilePath = path.resolve("package-info.java");
            if (Files.notExists(packageInfoFilePath)) {
                Files.createFile(packageInfoFilePath);
                Template template = freemarkerConfig.getTemplate("Domain.ftl");
                template.process(domainProperties, new OutputStreamWriter(Files.newOutputStream(packageInfoFilePath), StandardCharsets.UTF_8));
            }
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private void createNorthboundOhs(BoundedContextProperties boundedContextProperties) {
        NorthboundOhsProperties northboundOhsProperties = boundedContextProperties.getNorthboundOhsProperties();
        URI uri = URIUtil.INSTANCE.toURI(boundedContextProperties.getOutput(), StringUtil.INSTANCE.package2Path(northboundOhsProperties.getNorthboundOhsPackage()));
        Path path = Paths.get(uri);
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path);
            }
            Path packageInfoFilePath = path.resolve("package-info.java");
            if (Files.notExists(packageInfoFilePath)) {
                Files.createFile(packageInfoFilePath);
                Template template = freemarkerConfig.getTemplate("NorthBoundOhs.ftl");
                template.process(northboundOhsProperties, new OutputStreamWriter(Files.newOutputStream(packageInfoFilePath), StandardCharsets.UTF_8));
            }
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private void createSouthboundAcl(BoundedContextProperties boundedContextProperties) {
        SouthboundAclProperties southboundAclProperties = boundedContextProperties.getSouthboundAclProperties();
        URI uri = URIUtil.INSTANCE.toURI(boundedContextProperties.getOutput(), StringUtil.INSTANCE.package2Path(southboundAclProperties.getSouthboundAclPackage()));
        Path path = Paths.get(uri);
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path);
            }
            Path packageInfoFilePath = path.resolve("package-info.java");
            if (Files.notExists(packageInfoFilePath)) {
                Files.createFile(packageInfoFilePath);
                Template template = freemarkerConfig.getTemplate("SouthBoundAcl.ftl");
                template.process(southboundAclProperties, new OutputStreamWriter(Files.newOutputStream(packageInfoFilePath), StandardCharsets.UTF_8));
            }
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private void createAggregates(BoundedContextProperties boundedContextProperties) {
        for (AggregateProperties aggregateProperties : boundedContextProperties.getAggregatesProperties()) {
            createAggregate(boundedContextProperties.getOutput(), aggregateProperties);
            createEntities(boundedContextProperties.getOutput(), aggregateProperties);
        }
    }


    private void createAggregate(String output, AggregateProperties aggregateProperties) {
        URI uri = URIUtil.INSTANCE.toURI(output, StringUtil.INSTANCE.package2Path(aggregateProperties.getAggregatePackage()));
        Path path = Paths.get(uri);
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path);
            }
            Path packageInfoFilePath = path.resolve("package-info.java");
            if (Files.notExists(packageInfoFilePath)) {
                Files.createFile(packageInfoFilePath);
                Template template = freemarkerConfig.getTemplate("Aggregate.ftl");
                template.process(aggregateProperties, new OutputStreamWriter(Files.newOutputStream(packageInfoFilePath), StandardCharsets.UTF_8));
            }
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private void createEntities(String output, AggregateProperties aggregateProperties) {
        URI uri = URIUtil.INSTANCE.toURI(output, StringUtil.INSTANCE.package2Path(aggregateProperties.getAggregatePackage()));
        Path path = Paths.get(uri);
        for (EntityProperties entityProperties : aggregateProperties.getEntitiesProperties()) {
            createEntity(path, entityProperties);
        }
    }

    private void createEntity(Path path, EntityProperties entityProperties) {
        populateColumnMetas(entityProperties);
        try {
            Path packageInfoFilePath = path.resolve(entityProperties.getEntityName().concat(".java"));
            if (Files.notExists(packageInfoFilePath)) {
                Files.createFile(packageInfoFilePath);
                Template template = freemarkerConfig.getTemplate("Entity.ftl");
                template.process(entityProperties, new OutputStreamWriter(Files.newOutputStream(packageInfoFilePath), StandardCharsets.UTF_8));
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

