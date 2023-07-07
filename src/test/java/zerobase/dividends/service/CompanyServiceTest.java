package zerobase.dividends.service;

import org.apache.commons.collections4.Trie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import zerobase.dividends.exception.AbstractException;
import zerobase.dividends.exception.impl.AlreadyExistTickerException;
import zerobase.dividends.exception.impl.FailedToScrapTickerException;
import zerobase.dividends.model.Company;
import zerobase.dividends.model.ScrapedResult;
import zerobase.dividends.persist.CompanyRepository;
import zerobase.dividends.persist.DividendRepository;
import zerobase.dividends.persist.entity.CompanyEntity;
import zerobase.dividends.scraper.Scraper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Transactional
class CompanyServiceTest {
    public static final String ticker = "MMM";
    public static final String name = "3M Company (MMM)";
    @Mock
    private Trie trie;

    @Mock
    private Scraper yahooFinanceScraper;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private DividendRepository dividendRepository;

    @InjectMocks
    private CompanyService companyService;

    @Test
    @DisplayName("회사 저장 성공")
    void saveSuccess() {
        //given
        given(companyRepository.save(any()))
                .willReturn(new CompanyEntity(new Company(ticker, name)));
        given(yahooFinanceScraper.scrap(any()))
                .willReturn(new ScrapedResult(
                        new Company(ticker, name), new ArrayList<>()));
        given(companyRepository.existsByTicker(anyString()))
                .willReturn(false);
        given(yahooFinanceScraper.scrapCompanyByTicker(anyString()))
                .willReturn(new Company(ticker, name));

        ArgumentCaptor<CompanyEntity> captor =
                ArgumentCaptor.forClass(CompanyEntity.class);

        //when
        Company company = companyService.save(ticker);

        //then
        verify(companyRepository, times(1))
                .save(captor.capture());
        assertEquals("MMM", company.getTicker());
        assertEquals("3M Company (MMM)", company.getName());
    }

    @Test
    @DisplayName("이미 존재하는 ticker - 저장 실패")
    void save_AlreadyExistTicker() {
        //given
        given(companyRepository.existsByTicker(anyString()))
                .willReturn(true);

        AlreadyExistTickerException e = new AlreadyExistTickerException();

        //when
        AbstractException exception = assertThrows(AbstractException.class,
                () -> companyService.save(ticker));
        //then
        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode());
        assertEquals(e.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("ticker 스크랩 실패 - 저장 실패")
    void save_FailedToScrapTicker() {
        //given
        given(companyRepository.existsByTicker(anyString()))
                .willReturn(false);
        given(yahooFinanceScraper.scrapCompanyByTicker(anyString()))
                .willReturn(null);

        FailedToScrapTickerException e = new FailedToScrapTickerException();

        //when
        AbstractException exception = assertThrows(AbstractException.class,
                () -> companyService.save(ticker));

        //then
        assertEquals(e.getMessage(), exception.getMessage());
    }

    @Test
    void getAllCompanySuccess() {
        //given
        List<CompanyEntity> companyEntities = Arrays.asList(
                new CompanyEntity(new Company(ticker, name))
        );
        PageRequest request = PageRequest.of(0, 10);
        int start = (int) request.getOffset();
        int end = Math.min(start + request.getPageSize(), companyEntities.size());
        Page<CompanyEntity> companies = new PageImpl<>(
                companyEntities.subList(start, end), request,
                companyEntities.size());

        given(companyRepository.save(any()))
                .willReturn(new CompanyEntity(new Company(ticker, name)));
        given(yahooFinanceScraper.scrap(any()))
                .willReturn(new ScrapedResult(
                        new Company(ticker, name), new ArrayList<>()));
        given(yahooFinanceScraper.scrapCompanyByTicker(anyString()))
                .willReturn(new Company(ticker, name));

        //when
        companyService.save(ticker);
        Page<CompanyEntity> allCompany = companyService
                .getAllCompany(Pageable.ofSize(5));
        System.out.println(allCompany.stream()
                .collect(Collectors.toList())
                .size());

        //then
        assertEquals(allCompany.stream()
                        .collect(Collectors.toList())
                        .get(0).getTicker(),
                companies.stream()
                        .collect(Collectors.toList())
                        .get(0).getTicker());
    }
}