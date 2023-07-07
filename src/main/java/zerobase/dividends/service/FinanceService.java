package zerobase.dividends.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import zerobase.dividends.exception.impl.NoCompanyException;
import zerobase.dividends.model.Company;
import zerobase.dividends.model.Dividend;
import zerobase.dividends.model.ScrapedResult;
import zerobase.dividends.persist.CompanyRepository;
import zerobase.dividends.persist.DividendRepository;
import zerobase.dividends.persist.entity.CompanyEntity;
import zerobase.dividends.persist.entity.DividendEntity;

import java.util.List;
import java.util.stream.Collectors;

import static zerobase.dividends.model.constants.CacheKey.KEY_FINANCE;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Cacheable(key = "#companyName", value = KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        log.info("search company -> {}", companyName);
        // 회사명을 기준으로 회사정보 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(() ->
                        new NoCompanyException());

        // 조회된 회사의 ID 로 배당금 정보 조회
        List<DividendEntity> dividendEntities =
                this.dividendRepository.findAllByCompanyId(company.getId());

        // 결과 조합 후 반환
        List<Dividend> dividends = dividendEntities.stream()
                .map(e -> new Dividend(e.getDate(), e.getDividend()))
                .collect(Collectors.toList());

        return new ScrapedResult(new Company(company.getTicker(),
                company.getName()),
                dividends);
    }
}
