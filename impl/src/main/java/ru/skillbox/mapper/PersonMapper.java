package ru.skillbox.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.skillbox.model.Person;

import java.sql.ResultSet;
import java.sql.SQLException;
//todo удалить если не используется

public class PersonMapper implements RowMapper<Person> {

    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Person.builder()
                .id(rs.getLong("id"))
                .firstName(rs.getString("firstName"))
                .lastName(rs.getString("lastName"))
                .build();
    }
}

