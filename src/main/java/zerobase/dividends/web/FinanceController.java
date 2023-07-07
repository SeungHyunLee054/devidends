package zerobase.dividends.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.dividends.service.FinanceService;

@Slf4j
@RestController
@RequestMapping("/finance")
@AllArgsConstructor
public class FinanceController {
    private final FinanceService financeService;

    /**
     * 배당금 조회
     */
    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<?> searchFinance(@PathVariable String companyName) {
        var result = this.financeService.getDividendByCompanyName(companyName);

        log.info("dividends is searched -> {}", companyName);
        return ResponseEntity.ok(result);
    }
}
