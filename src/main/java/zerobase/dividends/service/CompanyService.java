package zerobase.dividends.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import zerobase.dividends.exception.impl.AlreadyExistTickerException;
import zerobase.dividends.exception.impl.FailedToScrapTickerException;
import zerobase.dividends.exception.impl.NoCompanyException;
import zerobase.dividends.model.Company;
import zerobase.dividends.model.ScrapedResult;
import zerobase.dividends.persist.CompanyRepository;
import zerobase.dividends.persist.DividendRepository;
import zerobase.dividends.persist.entity.CompanyEntity;
import zerobase.dividends.persist.entity.DividendEntity;
import zerobase.dividends.scraper.Scraper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CompanyService {
    private final Trie trie;
    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        log.info("save ticker -> {}", ticker);
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if (exists) {
            throw new AlreadyExistTickerException();
        }

        return storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        log.info("page number & page size -> {}, {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return this.companyRepository.findAll(pageable);
    }

    private Company storeCompanyAndDividend(String ticker) {
        // ticker 를 기준으로 회사를 스크래핑
        log.info("search ticker -> {}", ticker);
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new FailedToScrapTickerException();
        }

        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        // 스크래핑 결과
        CompanyEntity companyEntity =
                this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntities =
                scrapedResult.getDividends().stream()
                        .map(e -> new DividendEntity(companyEntity.getId(), e))
                        .collect(Collectors.toList());
        this.dividendRepository.saveAll(dividendEntities);

        return company;
    }

    // db 에서 직접 검색하여 자동완성
//    public List<String> getCompanyNamesByKeyword(String keyword) {
//        Pageable limit = PageRequest.of(0, 10);
//        Page<CompanyEntity> companyEntities =
//                this.companyRepository.
//                        findByNameStartingWithIgnoreCase(keyword, limit);
//
//        return companyEntities.stream()
//                .map(e -> e.getName())
//                .collect(Collectors.toList());
//    }

    // trie 에서 검색하여 자동완성
    public void addAutocompleteKeyword(String keyword) {
        this.trie.put(keyword, null);
    }

    public List<String> autocomplete(String keyword) {
        log.info("autocomplete keyword -> {}", keyword);
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    public void deleteAutocompleteKeyword(String keyword) {
        this.trie.remove(keyword);
    }

    public String deleteCompany(String ticker) {
        log.info("delete ticker -> {}", ticker);
        var company = this.companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new NoCompanyException());

        this.dividendRepository.deleteAllByCompanyId(company.getId());
        this.companyRepository.delete(company);

        this.deleteAutocompleteKeyword(company.getName());

        return company.getName();
    }
}
