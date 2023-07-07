package zerobase.dividends.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import zerobase.dividends.model.Company;
import zerobase.dividends.model.Dividend;
import zerobase.dividends.model.ScrapedResult;
import zerobase.dividends.service.FinanceService;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(FinanceController.class)
class FinanceControllerTest {
    @MockBean
    private FinanceService financeService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void searchFinanceSuccess() throws Exception {
        //given
        given(financeService.getDividendByCompanyName(anyString()))
                .willReturn(new ScrapedResult(new Company("a", "b"), Arrays.asList(
                        new Dividend(LocalDateTime.now(), "1.25"),
                        new Dividend(LocalDateTime.now(), "1.23")
                )));

        //when
        //then
        mockMvc.perform(get("/finance/dividend/b"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
        ;
    }
}