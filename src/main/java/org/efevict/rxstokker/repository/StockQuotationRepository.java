package org.efevict.rxstokker.repository;

import java.util.List;

import org.efevict.rxstokker.receiver.StockQuotation;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component
public interface StockQuotationRepository extends PagingAndSortingRepository<StockQuotation, Long> 
{
	List<StockQuotation> findByStock(@Param(value = "stock") String stock);
}
