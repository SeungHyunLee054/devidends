package zerobase.dividends.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zerobase.dividends.model.Company;
import zerobase.dividends.persist.CompanyRepository;
import zerobase.dividends.persist.DividendRepository;
import zerobase.dividends.persist.entity.CompanyEntity;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FinanceServiceTest {
    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private DividendRepository dividendRepository;

    @InjectMocks
    private FinanceService financeService;

    @Test
    void getDividendByCompanyNameSuccess() {
        //given
        given(companyRepository.findByName(anyString()))
                .willReturn(Optional.of(new CompanyEntity(new Company("a", "b"))));


        //when
        //then
    }
}