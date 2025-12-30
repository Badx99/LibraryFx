package service;

import DAO.BookRepository;
import DAO.BorrowRepository;
import DAO.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BorrowRepository borrowRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getGlobalStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalBooks = bookRepository.count();
        long availableBooks = bookRepository.findByDisponibleTrue().size();
        long totalMembers = memberRepository.count();
        long activeMembers = memberRepository.findByActifTrue().size();
        long currentBorrows = borrowRepository.findEmpruntsEnCours().size();
        long lateBorrows = borrowRepository.findEmpruntsEnRetard(LocalDate.now()).size();

        stats.put("totalBooks", totalBooks);
        stats.put("availableBooks", availableBooks);
        stats.put("totalMembers", totalMembers);
        stats.put("activeMembers", activeMembers);
        stats.put("currentBorrows", currentBorrows);
        stats.put("lateBorrows", lateBorrows);

        // Calculate total fines (estimative)
        double totalFines = borrowRepository.findEmpruntsEnRetard(LocalDate.now()).stream()
                .mapToDouble(b -> (b.getRetardJours() != null ? b.getRetardJours() : 0) * 0.50)
                .sum();
        stats.put("totalFines", totalFines);

        return stats;
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getBorrowsByMonth() {
        // Simple aggregation for the last 6 months
        return borrowRepository.findAll().stream()
                .filter(b -> b.getDateEmprunt() != null)
                .collect(Collectors.groupingBy(
                        b -> b.getDateEmprunt().getMonth().toString() + " " + b.getDateEmprunt().getYear(),
                        TreeMap::new,
                        Collectors.counting()));
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getBookDistributionByCategory() {
        return bookRepository.findAll().stream()
                .filter(b -> b.getCategorie() != null)
                .collect(Collectors.groupingBy(
                        entity.Book::getCategorie,
                        Collectors.counting()));
    }
}
