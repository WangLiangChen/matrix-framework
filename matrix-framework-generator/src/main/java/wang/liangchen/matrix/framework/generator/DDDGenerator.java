package wang.liangchen.matrix.framework.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.data.dao.criteria.ColumnMeta;
import wang.liangchen.matrix.framework.data.datasource.ConnectionManager;
import wang.liangchen.matrix.framework.data.datasource.MultiDataSourceContext;
import wang.liangchen.matrix.framework.springboot.env.EnvironmentContext;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang 2022-04-26 8:47
 */
public class DDDGenerator {
    private final StandaloneDao standaloneDao;
    private final static String SQL = "select * from %s where 1=0";
    private final static String JAVA = ".java";
    private final static String GENERATOR_CONFIG_FILE = "/codegenerator.xml";
    private static final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private final Configuration freemarkerConfig;

    public DDDGenerator(StandaloneDao standaloneDao) {
        this.standaloneDao = standaloneDao;
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
        this.freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/templates");
        this.freemarkerConfig.setDefaultEncoding("UTF-8");
        this.freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public void generate() {
        // 获取配置信息
        List<GeneratorProperties> generatorPropertiesList = resolveConfiguration();
        // 填充数据表信息
        generatorPropertiesList.forEach(generatorProperties -> {
            String datasource = generatorProperties.getDatasource();
            if (StringUtil.INSTANCE.isBlank(datasource) || MultiDataSourceContext.INSTANCE.PRIMARY_DATASOURCE_NAME.equals(datasource)) {
                populateColumnMetas(generatorProperties);
            } else {
                // 切换数据源
                MultiDataSourceContext.INSTANCE.set(datasource);
                try {
                    populateColumnMetas(generatorProperties);
                } finally {
                    MultiDataSourceContext.INSTANCE.clear();
                }
            }
            createContextPackage(generatorProperties);
            createDomain(generatorProperties);
            createEntity(generatorProperties);
            createManager(generatorProperties);
            createSouthboundAcl(generatorProperties);
            createPort(generatorProperties);
            createAdapter(generatorProperties);
            createNorthboundOhs(generatorProperties);
            createLocal(generatorProperties);
            createRemote(generatorProperties);
            createMessageContractPublishLanguagePackage(generatorProperties);
        });
    }

    private void createContextPackage(GeneratorProperties generatorProperties) {
        GeneratorTemplate generatorTemplate = (GeneratorTemplate) generatorProperties;
        generatorTemplate.setDomainPackage("domain");
        generatorTemplate.setManagerClassName(generatorTemplate.getEntityName() + "Manager");
        generatorTemplate.setSouthboundAclPackage("southbound_acl");
        generatorTemplate.setPortPackage("port");
        generatorTemplate.setAdapterPackage("adapter");
        generatorTemplate.setRepositoryPackage("repository");
        generatorTemplate.setClientPackage("client");
        generatorTemplate.setPublisherPackage("publisher");
        generatorTemplate.setNorthboundOhsPackage("northbound_ohs");
        generatorTemplate.setLocalPackage("local");
        generatorTemplate.setRemotePackage("remote");
        generatorTemplate.setControllerPackage("controller");
        generatorTemplate.setProviderPackage("provider");
        generatorTemplate.setResourcePackage("resource");
        generatorTemplate.setSubscriberPackage("subscriber");
        generatorTemplate.setMessageContractPublishLanguagePackage("message_pl");
        generatorTemplate.setNorthPackage("north");
        generatorTemplate.setSouthPackage("south");

        String contextPackagePathName = new StringBuilder().append(generatorProperties.getOutput()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorProperties.getContextPackage()).toString();
        Path contextPackagePath = Paths.get(contextPackagePathName);
        try {
            if (Files.notExists(contextPackagePath)) {
                Files.createDirectories(contextPackagePath);
            }
            Path packageInfoFilePath = contextPackagePath.resolve("package-info.java");
            Files.createFile(packageInfoFilePath);
            Template template = freemarkerConfig.getTemplate("BoundedContext.ftl");
            template.process(generatorProperties, new FileWriter(packageInfoFilePath.toFile()));
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private void createSouthboundAcl(GeneratorProperties generatorProperties) {
        GeneratorTemplate generatorTemplate = (GeneratorTemplate) generatorProperties;
        String packagePathName = new StringBuilder().append(generatorProperties.getOutput()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorProperties.getContextPackage()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getSouthboundAclPackage())
                .toString();
        Path packagePath = Paths.get(packagePathName);
        try {
            if (Files.notExists(packagePath)) {
                Files.createDirectories(packagePath);
            }
            Path packageInfoFilePath = packagePath.resolve("package-info.java");
            Files.createFile(packageInfoFilePath);
            Template template = freemarkerConfig.getTemplate("SouthBoundAcl.ftl");
            template.process(generatorProperties, new FileWriter(packageInfoFilePath.toFile()));
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private void createPort(GeneratorProperties generatorProperties) {
        GeneratorTemplate generatorTemplate = (GeneratorTemplate) generatorProperties;
        String packagePathName = new StringBuilder().append(generatorProperties.getOutput()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorProperties.getContextPackage()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getSouthboundAclPackage()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getPortPackage())
                .toString();
        String repositoryPackagePathName = new StringBuilder(packagePathName).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getRepositoryPackage())
                .toString();
        String clientPackagePathName = new StringBuilder(packagePathName).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getClientPackage())
                .toString();
        String publisherPackagePathName = new StringBuilder(packagePathName).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getPublisherPackage())
                .toString();
        Path packagePath = Paths.get(packagePathName);
        Path repositoryPackagePath = Paths.get(repositoryPackagePathName);
        Path clientPackagePath = Paths.get(clientPackagePathName);
        Path publisherPackagePath = Paths.get(publisherPackagePathName);
        try {
            if (Files.notExists(packagePath)) {
                Files.createDirectories(packagePath);
            }
            if (Files.notExists(repositoryPackagePath)) {
                Files.createDirectories(repositoryPackagePath);
            }
            if (Files.notExists(clientPackagePath)) {
                Files.createDirectories(clientPackagePath);
            }
            if (Files.notExists(publisherPackagePath)) {
                Files.createDirectories(publisherPackagePath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createAdapter(GeneratorProperties generatorProperties) {
        GeneratorTemplate generatorTemplate = (GeneratorTemplate) generatorProperties;
        String packagePathName = new StringBuilder().append(generatorProperties.getOutput()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorProperties.getContextPackage()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getSouthboundAclPackage()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getAdapterPackage())
                .toString();
        String repositoryPackagePathName = new StringBuilder(packagePathName).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getRepositoryPackage())
                .toString();
        String clientPackagePathName = new StringBuilder(packagePathName).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getClientPackage())
                .toString();
        String publisherPackagePathName = new StringBuilder(packagePathName).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getPublisherPackage())
                .toString();
        Path packagePath = Paths.get(packagePathName);
        Path repositoryPackagePath = Paths.get(repositoryPackagePathName);
        Path clientPackagePath = Paths.get(clientPackagePathName);
        Path publisherPackagePath = Paths.get(publisherPackagePathName);
        try {
            if (Files.notExists(packagePath)) {
                Files.createDirectories(packagePath);
            }
            if (Files.notExists(repositoryPackagePath)) {
                Files.createDirectories(repositoryPackagePath);
            }
            if (Files.notExists(clientPackagePath)) {
                Files.createDirectories(clientPackagePath);
            }
            if (Files.notExists(publisherPackagePath)) {
                Files.createDirectories(publisherPackagePath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createNorthboundOhs(GeneratorProperties generatorProperties) {
        GeneratorTemplate generatorTemplate = (GeneratorTemplate) generatorProperties;
        String packagePathName = new StringBuilder().append(generatorProperties.getOutput()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorProperties.getContextPackage()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getNorthboundOhsPackage())
                .toString();
        Path packagePath = Paths.get(packagePathName);
        try {
            if (Files.notExists(packagePath)) {
                Files.createDirectories(packagePath);
            }
            Path packageInfoFilePath = packagePath.resolve("package-info.java");
            Files.createFile(packageInfoFilePath);
            Template template = freemarkerConfig.getTemplate("NorthBoundOhs.ftl");
            template.process(generatorProperties, new FileWriter(packageInfoFilePath.toFile()));
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private void createLocal(GeneratorProperties generatorProperties) {
        GeneratorTemplate generatorTemplate = (GeneratorTemplate) generatorProperties;
        String packagePathName = new StringBuilder().append(generatorProperties.getOutput()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorProperties.getContextPackage()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getNorthboundOhsPackage()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getLocalPackage())
                .toString();
        Path packagePath = Paths.get(packagePathName);
        try {
            if (Files.notExists(packagePath)) {
                Files.createDirectories(packagePath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createRemote(GeneratorProperties generatorProperties) {
        GeneratorTemplate generatorTemplate = (GeneratorTemplate) generatorProperties;
        String packagePathName = new StringBuilder().append(generatorProperties.getOutput()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorProperties.getContextPackage()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getNorthboundOhsPackage()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getRemotePackage())
                .toString();
        String controllerPackagePathName = new StringBuilder(packagePathName).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getControllerPackage())
                .toString();
        String providerPackagePathName = new StringBuilder(packagePathName).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getProviderPackage())
                .toString();
        String resourcePackagePathName = new StringBuilder(packagePathName).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getResourcePackage())
                .toString();
        String subscriberPackagePathName = new StringBuilder(packagePathName).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getSubscriberPackage())
                .toString();
        Path packagePath = Paths.get(packagePathName);
        Path controllerPackagePath = Paths.get(controllerPackagePathName);
        Path providerPackagePath = Paths.get(providerPackagePathName);
        Path resourcePackagePath = Paths.get(resourcePackagePathName);
        Path subscriberPackagePath = Paths.get(subscriberPackagePathName);
        try {
            if (Files.notExists(packagePath)) {
                Files.createDirectories(packagePath);
            }
            if (Files.notExists(controllerPackagePath)) {
                Files.createDirectories(controllerPackagePath);
            }
            if (Files.notExists(providerPackagePath)) {
                Files.createDirectories(providerPackagePath);
            }
            if (Files.notExists(resourcePackagePath)) {
                Files.createDirectories(resourcePackagePath);
            }
            if (Files.notExists(subscriberPackagePath)) {
                Files.createDirectories(subscriberPackagePath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createMessageContractPublishLanguagePackage(GeneratorProperties generatorProperties) {
        GeneratorTemplate generatorTemplate = (GeneratorTemplate) generatorProperties;
        String packagePathName = new StringBuilder().append(generatorProperties.getOutput()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorProperties.getContextPackage()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getMessageContractPublishLanguagePackage())
                .toString();
        String northPackagePathName = new StringBuilder(packagePathName).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getNorthPackage())
                .toString();
        String southPackagePathName = new StringBuilder(packagePathName).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getSouthPackage())
                .toString();

        Path packagePath = Paths.get(packagePathName);
        Path northPackagePath = Paths.get(northPackagePathName);
        Path southPackagePath = Paths.get(southPackagePathName);
        try {
            if (Files.notExists(packagePath)) {
                Files.createDirectories(packagePath);
            }
            if (Files.notExists(northPackagePath)) {
                Files.createDirectories(northPackagePath);
            }
            if (Files.notExists(southPackagePath)) {
                Files.createDirectories(southPackagePath);
            }

            Path packageInfoFilePath = packagePath.resolve("package-info.java");
            Files.createFile(packageInfoFilePath);
            Template template = freemarkerConfig.getTemplate("MessageContractPublishLanguage.ftl");
            template.process(generatorProperties, new FileWriter(packageInfoFilePath.toFile()));
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private void createDomain(GeneratorProperties generatorProperties) {
        GeneratorTemplate generatorTemplate = (GeneratorTemplate) generatorProperties;
        String packagePathName = new StringBuilder().append(generatorProperties.getOutput()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorProperties.getContextPackage()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getDomainPackage()).toString();
        try {
            Path packagePath = Paths.get(packagePathName);
            if (Files.notExists(packagePath)) {
                Files.createDirectories(packagePath);
            }
            Path packageInfoFilePath = packagePath.resolve("package-info.java");
            Files.createFile(packageInfoFilePath);
            Template template = freemarkerConfig.getTemplate("Domain.ftl");
            template.process(generatorProperties, new FileWriter(packageInfoFilePath.toFile()));
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private void createEntity(GeneratorProperties generatorProperties) {
        GeneratorTemplate generatorTemplate = (GeneratorTemplate) generatorProperties;
        String entityPathName = new StringBuilder().append(generatorProperties.getOutput()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorProperties.getContextPackage()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getDomainPackage()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getAggregatePackage())
                .toString();
        try {
            Path entityPath = Paths.get(entityPathName);
            if (Files.notExists(entityPath)) {
                Files.createDirectories(entityPath);
            }
            Path packageInfoFilePath = entityPath.resolve("package-info.java");
            Files.createFile(packageInfoFilePath);
            Template template = freemarkerConfig.getTemplate("Aggregate.ftl");
            template.process(generatorProperties, new FileWriter(packageInfoFilePath.toFile()));

            // Entity File
            Path entityFilePath = entityPath.resolve(generatorProperties.getEntityName() + JAVA);
            if (Files.exists(entityFilePath)) {
                throw new MatrixWarnException("file:{} already exists", entityFilePath.toString());
            }
            Files.createFile(entityFilePath);
            template = freemarkerConfig.getTemplate("Entity.ftl");
            template.process(generatorProperties, new FileWriter(entityFilePath.toFile()));
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private void createManager(GeneratorProperties generatorProperties) {
        GeneratorTemplate generatorTemplate = (GeneratorTemplate) generatorProperties;
        String packagePathName = new StringBuilder().append(generatorProperties.getOutput()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorProperties.getContextPackage()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getDomainPackage()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getAggregatePackage())
                .toString();
        try {
            Path packagePath = Paths.get(packagePathName);
            if (Files.notExists(packagePath)) {
                Files.createDirectories(packagePath);
            }
            // Entity File
            Path entityFilePath = packagePath.resolve(generatorProperties.getEntityName() + "Manager" + JAVA);
            if (Files.exists(entityFilePath)) {
                throw new MatrixWarnException("file:{} already exists", entityFilePath.toString());
            }
            Files.createFile(entityFilePath);
            Template template = freemarkerConfig.getTemplate("Manager.ftl");
            template.process(generatorProperties, new FileWriter(entityFilePath.toFile()));
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private List<GeneratorProperties> resolveConfiguration() {
        String location = EnvironmentContext.INSTANCE.getLocation(GENERATOR_CONFIG_FILE);
        Resource resource = resourcePatternResolver.getResource(location);
        Document document;
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = documentBuilder.parse(resource.getURI().toString());
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }

        Element element = document.getDocumentElement();
        String author = element.getAttribute("author");
        String basePackage = element.getAttribute("base-package");
        String output = element.getAttribute("output");
        if (StringUtil.INSTANCE.isBlank(output)) {
            output = new StringBuilder().append(Symbol.USER_DIR.getSymbol()).append(Symbol.FILE_SEPARATOR.getSymbol())
                    .append("src").append(Symbol.FILE_SEPARATOR.getSymbol())
                    .append("main").append(Symbol.FILE_SEPARATOR.getSymbol())
                    .append("java").toString();
        }
        output = new StringBuilder(output).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(StringUtil.INSTANCE.package2Path(basePackage)).toString();

        NodeList nodes = document.getElementsByTagName("entity");
        int length = nodes.getLength();
        // String datasource, tableName, entityName, contextPackage, columnVersion, columnMarkDelete, columnMarkDeleteValue;
        GeneratorProperties generatorProperties;
        List<GeneratorProperties> generatorPropertiesList = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            Node node = nodes.item(i);
            String nodeName = node.getNodeName();
            if (!"entity".equals(nodeName)) {
                continue;
            }
            generatorProperties = new GeneratorTemplate();
            generatorProperties.setAuthor(author);
            generatorProperties.setOutput(output);
            generatorProperties.setBasePackage(basePackage);
            // 默认下划线转驼峰
            generatorProperties.setCamelCase(true);

            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                node = childNodes.item(j);
                nodeName = node.getNodeName();
                switch (nodeName) {
                    case "datasource":
                        generatorProperties.setDatasource(node.getTextContent());
                        break;
                    case "table-name":
                        String tableName = node.getTextContent();
                        generatorProperties.setTableName(tableName);
                        generatorProperties.setEntityName(StringUtil.INSTANCE.underline2UpperCamelCase(tableName));
                        break;
                    case "entity-name":
                        generatorProperties.setEntityName(node.getTextContent());
                        break;
                    case "context-package":
                        generatorProperties.setContextPackage(node.getTextContent());
                        break;
                    case "aggregate-package":
                        generatorProperties.setAggregatePackage(node.getTextContent());
                        break;
                    case "aggregate-root":
                        generatorProperties.setAggregateRoot(Boolean.parseBoolean(node.getTextContent()));
                        break;
                    case "column-version":
                        generatorProperties.setColumnVersion(node.getTextContent());
                        break;
                    case "column-state":
                        generatorProperties.setColumnState(node.getTextContent());
                        generatorProperties.setColumnStateUseConstantEnum(Boolean.parseBoolean(node.getAttributes().getNamedItem("useConstantEnum").getTextContent()));
                        break;
                    case "column-json":
                        generatorProperties.setColumnJson(node.getTextContent());
                        break;
                    case "column-markdelete":
                        generatorProperties.setColumnMarkDelete(node.getTextContent());
                        generatorProperties.setColumnMarkDeleteValue(node.getAttributes().getNamedItem("value").getTextContent());
                        break;
                    case "camel-case":
                        generatorProperties.setCamelCase(Boolean.parseBoolean(node.getTextContent()));
                        break;

                }
            }
            generatorPropertiesList.add(generatorProperties);
        }
        return generatorPropertiesList;

    }

    private void populateColumnMetas(GeneratorProperties generatorProperties) {
        String tableName = generatorProperties.getTableName();
        ConnectionManager.INSTANCE.executeInNonManagedConnection((connection) -> {
            try {
                // 查询主键唯一键等信息
                DatabaseMetaData databaseMetaData = connection.getMetaData();
                List<String> primaryKeyColumnNames = primaryKeyColumnNames(databaseMetaData, tableName);
                List<String> uniqueKeyColumnNames = uniqueKeyColumnNames(databaseMetaData, tableName);
                uniqueKeyColumnNames.removeAll(primaryKeyColumnNames);

                List<ColumnMeta> columnMetas = resolveResultSetMetaData(connection, tableName, generatorProperties, primaryKeyColumnNames, uniqueKeyColumnNames);
                GeneratorTemplate generatorTemplate = (GeneratorTemplate) generatorProperties;
                generatorTemplate.getColumnMetas().addAll(columnMetas);
                generatorTemplate.getPkColumnMetas().addAll(columnMetas.stream().filter(ColumnMeta::isId).collect(Collectors.toList()));
                columnMetas.stream().filter(ColumnMeta::isState)
                        .findFirst().ifPresent(generatorTemplate::setStateColumnMeta);

                // 构造imports
                Set<String> imports = columnMetas.stream().map(ColumnMeta::getImportPackage).filter(StringUtil.INSTANCE::isNotBlank).collect(Collectors.toSet());
                generatorTemplate.getImports().addAll(imports);
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
        ResultSet resultSet = databaseMetaData.getPrimaryKeys(null, null, tableName);
        while (resultSet.next()) {
            String columnName = resultSet.getString("COLUMN_NAME");
            uniqueKeyColumnNames.add(columnName);
        }
        resultSet.close();
        return uniqueKeyColumnNames;

    }

    private List<ColumnMeta> resolveResultSetMetaData(Connection connection, String tableName, GeneratorProperties generatorProperties, List<String> primaryKeyColumnNames, List<String> uniqueKeyColumnNames) throws SQLException {
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
            boolean isVersion = columnName.equals(generatorProperties.getColumnVersion());
            boolean isJson = columnName.equals(generatorProperties.getColumnJson());
            boolean isState = columnName.equals(generatorProperties.getColumnState());
            boolean isStateUseConstantEnum = generatorProperties.isColumnStateUseConstantEnum();
            String _deleteValue = columnName.equals(generatorProperties.getColumnMarkDelete()) ? generatorProperties.getColumnMarkDeleteValue() : null;

            columnMeta = ColumnMeta.newInstance(columnName, dataTypeName, jdbcTypeName, isId, isUnique, isVersion, isJson, isState, isStateUseConstantEnum, _deleteValue, generatorProperties.isCamelCase());
            columnMetas.add(columnMeta);
        }
        preparedStatement.close();
        resultSet.close();
        return columnMetas;
    }
}
