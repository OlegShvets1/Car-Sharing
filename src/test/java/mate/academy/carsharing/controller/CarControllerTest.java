package mate.academy.carsharing.controller;

import static mate.academy.carsharing.model.Car.Type.UNIVERSAL;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.carsharing.dto.car.CarResponseDto;
import mate.academy.carsharing.dto.car.CreateCarRequestDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/cars/add-cars-to-cars-table.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/cars/remove-cars-from-cars-table.sql")
            );
        }
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    void createCar_ValidRequestDto_Ok() throws Exception {
        CreateCarRequestDto requestDto = new CreateCarRequestDto(
                "Model S",
                "Tesla",
                "UNIVERSAL",
                5,
                BigDecimal.valueOf(299)
        );
        CarResponseDto expected = new CarResponseDto(
                1L,
                "Model S",
                "Tesla",
                UNIVERSAL,
                5,
                BigDecimal.valueOf(229)
        );

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/cars")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CarResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarResponseDto.class
        );

        assertNotNull(actual);
        assertNotNull(actual.id());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    void createCar_InvalidRequestDto_NotOk() throws Exception {
        CreateCarRequestDto requestDto = new CreateCarRequestDto(
                "",
                "",
                "",
                -1,
                BigDecimal.valueOf(-299)
        );

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/cars")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    void getCarById_ValidId_Ok() throws Exception {
        CarResponseDto expected = new CarResponseDto(
                1L,
                "Model S",
                "Tesla",
                UNIVERSAL,
                5,
                BigDecimal.valueOf(229)
        );

        MvcResult result = mockMvc.perform(get("/cars/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarResponseDto.class);

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    void getCarById_NotExistsId_NotFound() throws Exception {
        mockMvc.perform(get("/cars/-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
