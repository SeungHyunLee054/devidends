package zerobase.dividends.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import zerobase.dividends.model.Company;
import zerobase.dividends.service.CompanyService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CompanyController.class)
class CompanyControllerTest {
    @MockBean
    private CompanyService companyService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void successSave() throws Exception {
        //given
        given(companyService.save(anyString()))
                .willReturn(new Company("MMM", "b"));
        //when
        //then
        mockMvc.perform(post("/company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                "MMM"
                        )))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.ticker").value("MMM"))
                .andDo(print());
    }
}