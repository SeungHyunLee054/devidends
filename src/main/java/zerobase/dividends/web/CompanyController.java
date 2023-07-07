package zerobase.dividends.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import zerobase.dividends.exception.impl.TickerIsEmptyException;
import zerobase.dividends.model.Company;
import zerobase.dividends.model.constants.CacheKey;
import zerobase.dividends.persist.entity.CompanyEntity;
import zerobase.dividends.service.CompanyService;

@Slf4j
@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    private final CacheManager cacheManager;

    /**
     * 자동 완성
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {
        // trie 에서 검색하여 자동완성
        var result = companyService.autocomplete(keyword);
        // db 에서 검색하여 자동완성
//        var result = companyService.getCompanyNamesByKeyword(keyword);

        log.info("keyword is autocomplete -> {}", keyword);
        return ResponseEntity.ok(result);
    }

    /**
     * 전체 회사 조회
     */
    @GetMapping
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> searchCompany(final Pageable pageable) {
        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);

        log.info("all companies are searched");
        return ResponseEntity.ok(companies);
    }

    /**
     * 회사 및 배당금 정보 추가
     */
    @PostMapping
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        String ticker = request.getTicker().trim();
        if (ObjectUtils.isEmpty(ticker)) {
            throw new TickerIsEmptyException();
        }

        Company company = this.companyService.save(ticker);

        this.companyService.addAutocompleteKeyword(company.getName());

        log.info("company and dividends are added -> {}", company.getName());
        return ResponseEntity.ok(company);
    }

    /**
     * 회사 및 배당금 정보 및 캐쉬 삭제
     */
    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {
        String companyName = this.companyService.deleteCompany(ticker);
        this.clearFinanceCache(companyName);

        log.info("company is deleted -> {}", companyName);
        return ResponseEntity.ok(companyName);
    }

    public void clearFinanceCache(String companyName) {
        this.cacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyName);
    }
}
