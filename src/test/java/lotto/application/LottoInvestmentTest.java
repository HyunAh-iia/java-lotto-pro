package lotto.application;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import lotto.domain.wrapper.LottoOrderRequest;
import lotto.domain.wrapper.LottoNumber;
import lotto.domain.wrapper.LottoTicket;
import lotto.domain.wrapper.Money;

public class LottoInvestmentTest extends LottoInvestment {
	private final LottoInvestment lottoInvestment = new LottoInvestment();


	@DisplayName("로또 5장 구입 시 투자금 검증")
	@Test
	void totalInvestmentTest() {
		// given
		final int ORDER_COUNT = 5;

		// when
		this.lottoInvestment.buyTicket(LottoOrderRequest.byOrderCount(ORDER_COUNT));

		// then
		assertThat(this.lottoInvestment.totalInvestment()).isEqualTo(new Money(LottoTicket.PRICE * ORDER_COUNT));
	}

	@DisplayName("로또번호 적중 개수별 투자수익율")
	@ParameterizedTest
	@CsvSource(value = {
		"1,-1",
		"2,-1",
		"3,4",
		"4,49",
		"5,1499",
		"6,1999999",
	})
	void analysisProfitTest(int matchedNumberCount, BigDecimal expectedProfit) {
		final int ORDER_COUNT = 1;
		final int FIRST = 0;
		final List<LottoNumber> defaultNumbers = LottoTicket.getDefaultNumbers();

		this.lottoInvestment.buyTicket(LottoOrderRequest.byOrderCount(ORDER_COUNT));
		List<LottoNumber> holdLottoNumbers = lottoInvestment.holdings().get(FIRST).getNumbers();
		defaultNumbers.removeAll(holdLottoNumbers);
		List<LottoNumber> matchedNumbersInHoldTicket = holdLottoNumbers.subList(FIRST, matchedNumberCount);
		List<LottoNumber> lastWeekWinningNumbers = new ArrayList<>(matchedNumbersInHoldTicket);
		lastWeekWinningNumbers.addAll(defaultNumbers.subList(FIRST, LottoTicket.NUMBER_COUNT - lastWeekWinningNumbers.size()));
		lottoInvestment.findLastWinningTicket(new LottoTicket(lastWeekWinningNumbers));

		assertThat(lottoInvestment.analysisProfit()).isEqualTo(expectedProfit);
	}
}