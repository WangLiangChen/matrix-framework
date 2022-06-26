package wang.liangchen.matrix.framework.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.stereotype.Component;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.data.annotation.IdStrategy;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.data.dao.criteria.ColumnMeta;
import wang.liangchen.matrix.framework.data.datasource.ConnectionsManager;
import wang.liangchen.matrix.framework.data.datasource.MultiDataSourceContext;

import javax.inject.Inject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang 2022-04-26 8:47
 */
@Component
public class DomainGenerator {
    private final StandaloneDao standaloneDao;
    private final static String SQL = "select * from %s where 1=0";
    private final static String JAVA = ".java";
    private final static String GENERATOR_CONFIG_FILE = "codegenerator.xml";
    private final Configuration freemarkerConfig;

    @Inject
    public DomainGenerator(StandaloneDao standaloneDao) {
        this.standaloneDao = standaloneDao;
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
        this.freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/templates");
        this.freemarkerConfig.setDefaultEncoding("UTF-8");
        this.freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public void build() {
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
            createDomain(generatorProperties);
        });
    }

    private void createDomain(GeneratorProperties generatorProperties) {
        GeneratorTemplate generatorTemplate = (GeneratorTemplate) generatorProperties;
        generatorTemplate.setDomainPackage("domain");
        createEntity(generatorProperties);
    }

    private void createEntity(GeneratorProperties generatorProperties) {
        GeneratorTemplate generatorTemplate = (GeneratorTemplate) generatorProperties;
        String entityPathName = new StringBuilder().append(generatorProperties.getOutput()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorProperties.getContextPackage()).append(Symbol.FILE_SEPARATOR.getSymbol())
                .append(generatorTemplate.getDomainPackage()).toString();
        try {
            Path entityPath = Paths.get(entityPathName);
            if (Files.notExists(entityPath)) {
                Files.createDirectories(entityPath);
            }
            // Entity File
            Path entityFilePath = entityPath.resolve(generatorProperties.getEntityName() + JAVA);
            if (Files.exists(entityFilePath)) {
                throw new MatrixInfoException("file:{} already exists", entityFilePath.toString());
            }
            Files.createFile(entityFilePath);
            Template template = freemarkerConfig.getTemplate("Entity.ftl");
            template.process(generatorProperties, new FileWriter(entityFilePath.toFile()));
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private List<GeneratorProperties> resolveConfiguration() {
        /*XMLConfiguration generatorXml = (XMLConfiguration) ConfigurationContext.INSTANCE.resolve(GENERATOR_CONFIG_FILE);
        Document document = generatorXml.getDocument();
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
        String datasource, tableName, entityName, contextPackage, columnVersion, columnMarkDelete, columnMarkDeleteValue;
        GeneratorProperties generatorProperties;
        List<GeneratorProperties> generatorPropertiesList = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            generatorProperties = new GeneratorTemplate();
            generatorProperties.setAuthor(author);
            generatorProperties.setOutput(output);
            generatorProperties.setBasePackage(basePackage);

            datasource = generatorXml.getString(String.format("entity(%d).datasource", i));
            generatorProperties.setDatasource(datasource);
            tableName = generatorXml.getString(String.format("entity(%d).table-name", i));
            generatorProperties.setTableName(tableName);
            entityName = generatorXml.getString(String.format("entity(%d).entity-name", i));
            generatorProperties.setEntityName(entityName);
            contextPackage = generatorXml.getString(String.format("entity(%d).context-package", i));
            generatorProperties.setContextPackage(contextPackage);
            columnVersion = generatorXml.getString(String.format("entity(%d).column-version", i));
            generatorProperties.setColumnVersion(columnVersion);
            columnMarkDelete = generatorXml.getString(String.format("entity(%d).column-markdelete", i));
            generatorProperties.setColumnMarkDelete(columnMarkDelete);
            columnMarkDeleteValue = generatorXml.getString(String.format("entity(%d).column-markdelete[@value]", i));
            generatorProperties.setColumnMarkDeleteValue(columnMarkDeleteValue);
            boolean camelCase = generatorXml.getBoolean(String.format("entity(%d).camel-case", i), Boolean.FALSE);
            generatorProperties.setCamelCase(camelCase);
            generatorPropertiesList.add(generatorProperties);
        }
        return generatorPropertiesList;*/
        return Collections.emptyList();
    }

    private void populateColumnMetas(GeneratorProperties generatorProperties) {
        String tableName = generatorProperties.getTableName();
        ConnectionsManager.INSTANCE.executeInNonManagedConnection((connection) -> {
            try {
                // 查询主键唯一键等信息
                DatabaseMetaData databaseMetaData = connection.getMetaData();
                List<String> primaryKeyColumnNames = primaryKeyColumnNames(databaseMetaData, tableName);
                List<String> uniqueKeyColumnNames = uniqueKeyColumnNames(databaseMetaData, tableName);
                uniqueKeyColumnNames.removeAll(primaryKeyColumnNames);

                List<ColumnMeta> columnMetas = resolveResultSetMetaData(connection, tableName, generatorProperties.isCamelCase(), generatorProperties.getColumnVersion(), generatorProperties.getColumnMarkDelete(), generatorProperties.getColumnMarkDeleteValue(), primaryKeyColumnNames, uniqueKeyColumnNames);
                GeneratorTemplate generatorTemplate = (GeneratorTemplate) generatorProperties;
                generatorTemplate.getColumnMetas().addAll(columnMetas);
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

    private List<ColumnMeta> resolveResultSetMetaData(Connection connection, String tableName, boolean underline2camelCase, String versionColumn, String deleteColumn, String markDeleteValue, List<String> primaryKeyColumnNames, List<String> uniqueKeyColumnNames) throws SQLException {
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
            boolean isVersion = columnName.equals(versionColumn);
            String _deleteValue = columnName.equals(deleteColumn) ? markDeleteValue : null;

            columnMeta = ColumnMeta.newInstance(columnName, dataTypeName, jdbcTypeName, isId, IdStrategy.NONE, isUnique, isVersion, _deleteValue, underline2camelCase);
            columnMetas.add(columnMeta);
        }
        preparedStatement.close();
        resultSet.close();
        return columnMetas;
    }
}
