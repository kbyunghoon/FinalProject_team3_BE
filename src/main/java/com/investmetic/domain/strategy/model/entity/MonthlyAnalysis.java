package com.investmetic.domain.strategy.model.entity;

import com.investmetic.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MonthlyAnalysis extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "monthly_analysis_id")
    private Long monthlyAnalysisId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    private LocalDate monthlyDate; // YYYY-DD

    private Long monthlyAveragePrincipal; //  월평균 원금

    private Long depositsWithdrawals; // 입출금

    private Long monthlyProfitLoss; // 월 손익

    private Double monthlyProfitLossRate; // 월 손익률

    private Long cumulativeProfitLoss; // 누적 손익

    private Double cumulativeProfitLossRate; // 누적 손익률

    public void setMonthlyAnalysisData(Long monthlyAveragePrincipal, Long depositsWithdrawals, Long monthlyProfitLoss,
                                       Double monthlyProfitLossRate, Long cumulativeProfitLoss,
                                       Double cumulativeProfitLossRate
    ) {
        this.monthlyAveragePrincipal = monthlyAveragePrincipal;
        this.depositsWithdrawals = depositsWithdrawals;
        this.monthlyProfitLoss = monthlyProfitLoss;
        this.monthlyProfitLossRate = monthlyProfitLossRate;
        this.cumulativeProfitLoss = cumulativeProfitLoss;
        this.cumulativeProfitLossRate = cumulativeProfitLossRate;
    }
}