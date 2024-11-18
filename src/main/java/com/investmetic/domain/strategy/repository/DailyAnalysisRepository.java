package com.investmetic.domain.strategy.repository;

import com.investmetic.domain.strategy.model.entity.DailyAnalysis;
import com.investmetic.domain.strategy.model.entity.Strategy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DailyAnalysisRepository extends JpaRepository<DailyAnalysis, Long>, DailyAnalysisRepositoryCustom {
    @Query("SELECT d FROM DailyAnalysis d WHERE d.strategy.strategyId = :strategyId")
    Page<DailyAnalysis> findByStrategyId(@Param("strategyId") Long strategyId, Pageable pageable);

    boolean existsByStrategyAndDailyDate(Strategy strategy, LocalDate dailyDate);

    // 특정 전략의 해당 날짜의 이전 데이터들 가져오기
    @Query("SELECT d FROM DailyAnalysis d WHERE d.strategy.strategyId = :strategyId AND d.dailyDate < :startDate ORDER BY d.dailyDate ASC")
    List<DailyAnalysis> findAllByStrategyAndDateBefore(@Param("strategyId") Long strategyId,
                                                       @Param("startDate") LocalDateTime startDate);

    // 특정 전략의 가장 오래된 updated_at 이후 데이터를 가져오기
    @Query("SELECT d FROM DailyAnalysis d WHERE d.strategy.strategyId = :strategyId AND d.dailyDate >= :startDate ORDER BY d.dailyDate ASC")
    List<DailyAnalysis> findAllByStrategyAndDateAfter(@Param("strategyId") Long strategyId,
                                                      @Param("startDate") LocalDateTime startDate);

    // 특정 전략의 가장 오래된 updated_at 날짜 조회
    @Query("SELECT MIN(d.updatedAt) FROM DailyAnalysis d WHERE d.strategy.strategyId = :strategyId AND d.updatedAt > :lastRunDate")
    Optional<LocalDateTime> findOldestUpdatedAtAfter(@Param("strategyId") Long strategyId,
                                                     @Param("lastRunDate") LocalDateTime lastRunDate);

    // 전날 데이터 조회
    @Query("SELECT d FROM DailyAnalysis d WHERE d.strategy.strategyId = :strategyId AND d.dailyDate < :currentDate ORDER BY d.dailyDate DESC LIMIT 1")
    Optional<DailyAnalysis> findLatestBefore(@Param("strategyId") Long strategyId,
                                             @Param("currentDate") LocalDate currentDate);

    /**
     * 특정 전략 ID로 DailyAnalysis 데이터를 조회
     */
    @Query("SELECT d FROM DailyAnalysis d WHERE d.strategy.strategyId = :strategyId ORDER BY d.dailyDate ASC")
    List<DailyAnalysis> findAllByStrategy(@Param("strategyId") Long strategyId);
}