package com.List.ToDoList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TaskRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert taskInserter;

    @Autowired
    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.taskInserter = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("task")
                .usingGeneratedKeyColumns("id");
        createTable();
    }

    private void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS task (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    description VARCHAR(255) NOT NULL,
                    completed BOOLEAN NOT NULL,
                    created_at TIMESTAMP NOT NULL
                )
                """;
        jdbcTemplate.execute(sql);
    }

    public void insertTask(Task task) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("description", task.getDescription());
        parameters.put("completed", task.isCompleted());
        parameters.put("created_at", new Timestamp(task.getCreatedAt().getTime()));
        Number newId = taskInserter.executeAndReturnKey(parameters);
        task.setId(newId.longValue());
    }

    public Task selectTask(long id) {
        String sql = "SELECT * FROM task WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new TaskRowMapper(), id);
    }

    public List<Task> selectAllTasks() {
        String sql = "SELECT * FROM task";
        return jdbcTemplate.query(sql, new TaskRowMapper());
    }

    public void updateTask(Task task) {
        String sql = "UPDATE task SET completed = ? WHERE id = ?";
        jdbcTemplate.update(sql, task.isCompleted(), task.getId());
    }

    private static class TaskRowMapper implements RowMapper<Task> {
        @Override
        public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
            Task task = new Task();
            task.setId(rs.getLong("id"));
            task.setDescription(rs.getString("description"));
            task.setCompleted(rs.getBoolean("completed"));
            task.setCreatedAt(rs.getTimestamp("created_at"));
            return task;
        }
    }
}
