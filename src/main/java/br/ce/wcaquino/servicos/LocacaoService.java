package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.servicos.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.servicos.exception.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {

	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocadoraException {

		if(usuario == null) {
			throw new LocadoraException("Usuario vazio");
		}

		if(filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Filme vazio");
		}

		Double totalPrecoLocacao = 0d;
		for( Filme filme : filmes) {
			if(filme == null) {
				throw new LocadoraException("Filme vazio");
			}

			if(filme.getEstoque() == 0) {
				throw new FilmeSemEstoqueException("Filme sem estoque");
			}
			totalPrecoLocacao += filme.getPrecoLocacao();
		}

		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		locacao.setTotalPrecoLocacao(totalPrecoLocacao);
		locacao.setTotalDescontos(getValorTotalDescontos(filmes));
		locacao.setValorPagamento(locacao.getTotalPrecoLocacao() - locacao.getTotalDescontos());

		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		if(isDomingo(dataEntrega)){
			dataEntrega = adicionarDias(dataEntrega, 1);
		}
		locacao.setDataRetorno(dataEntrega);

		//Salvando a locacao...
		//TODO adicionar método para salvar

		return locacao;
	}

	private boolean isDomingo(Date dataEntrega) {
		return DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY);
	}

	private Double getValorTotalDescontos(List<Filme> filmes) {
		Double desconto = 0.00;
		for (Filme filme : filmes) {
			desconto += filme.getPrecoLocacao() * getPercentualDesconto(filmes.indexOf(filme));
		}
		return desconto;
	}

	private Double getPercentualDesconto(int posicao) {
		Double[] desconto = {0d, 0d, 25d, 50d, 75d, 100d };
		return posicao > (desconto.length - 1) ? 0 : desconto[posicao] / 100;
	}

}