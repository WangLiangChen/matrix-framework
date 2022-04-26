package wang.liangchen.matrix.framework.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.stereotype.Component;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.data.dao.criteria.ColumnMeta;
import wang.liangchen.matrix.framework.data.datasource.ConnectionsManager;
import wang.liangchen.matrix.framework.data.util.DatabaseUtil;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Liangchen.Wang 2022-04-26 8:47
 */
@Component
public class DomainGenerator {
    private final StandaloneDao standaloneDao;
    private final String SQL = "select * from %s where 1=0";

    @Inject
    public DomainGenerator(StandaloneDao standaloneDao) {
        this.standaloneDao = standaloneDao;
    }

    public void doIt(String tableName,String versionColumn, String deleteColumn, String deleteValue) throws IOException, TemplateException {
        List<ColumnMeta> columnMetas = this.columnMetas(tableName, true, versionColumn, deleteColumn, deleteValue);
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        //cfg.setClassForTemplateLoading(EntityTemplate.class,"template");
        cfg.setClassForTemplateLoading(this.getClass(), "/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        Template template = cfg.getTemplate("Entity.ftl");
        EntityTemplate entityTemplate = new EntityTemplate();
        entityTemplate.setTableName(tableName);
        entityTemplate.setEntityName("AuthorizationSubject");
        entityTemplate.setBasePackage("com.sintrue.matrix.example");
        entityTemplate.setSubPackage("com.sintrue.matrix.example.domain");
        entityTemplate.setColumnMetas(columnMetas);
        template.process(entityTemplate, new OutputStreamWriter(System.out));
    }

    private List<ColumnMeta> columnMetas(String tableName, boolean underline2camelCase, String versionColumn, String deleteColumn, String deleteValue) {
        return ConnectionsManager.INSTANCE.executeInNonManagedConnection((connection) -> {
            try {
                DatabaseMetaData databaseMetaData = connection.getMetaData();
                List<String> primaryKeyColumnNames = primaryKeyColumnNames(databaseMetaData, tableName);
                List<String> uniqueKeyColumnNames = uniqueKeyColumnNames(databaseMetaData, tableName);
                uniqueKeyColumnNames.removeAll(primaryKeyColumnNames);
                List<ColumnMeta> columnMetas = resolveResultSetMetaData(connection, tableName, underline2camelCase, versionColumn, deleteColumn, deleteValue, primaryKeyColumnNames, uniqueKeyColumnNames);
                return columnMetas;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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

    private List<ColumnMeta> resolveResultSetMetaData(Connection connection, String tableName, boolean underline2camelCase, String versionColumn, String deleteColumn, String deleteValue, List<String> primaryKeyColumnNames, List<String> uniqueKeyColumnNames) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL, tableName));
        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        String columnName, fieldName, jdbcTypeName, javaTypeName;
        ColumnMeta columnMeta;
        List<ColumnMeta> columnMetas = new ArrayList<>();
        for (int i = 1, j = resultSetMetaData.getColumnCount(); i <= j; i++) {
            columnName = resultSetMetaData.getColumnName(i);
            jdbcTypeName = resultSetMetaData.getColumnTypeName(i);
            javaTypeName = DatabaseUtil.INSTANCE.jdbcType2JavaType(jdbcTypeName);
            fieldName = columnName;
            if (underline2camelCase) {
                fieldName = StringUtil.INSTANCE.underline2camelCase(columnName);
            }
            boolean isId = primaryKeyColumnNames.contains(columnName);
            boolean isUnique = uniqueKeyColumnNames.contains(columnName);
            boolean isVersion = columnName.equals(versionColumn);
            String _deleteValue = columnName.equals(deleteColumn) ? deleteValue : null;

            columnMeta = ColumnMeta.newInstance(fieldName, javaTypeName, columnName, isId, isUnique, isVersion, _deleteValue);
            columnMetas.add(columnMeta);
        }
        preparedStatement.close();
        resultSet.close();
        return columnMetas;
    }
}
