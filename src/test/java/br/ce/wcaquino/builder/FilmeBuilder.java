package br.ce.wcaquino.builder;

import br.ce.wcaquino.entidades.Filme;

public class FilmeBuilder {

    private Filme filme;

    private FilmeBuilder() {
    }

    public static FilmeBuilder umFilme() {
        FilmeBuilder builder = new FilmeBuilder();
        builder.filme = new Filme("Filme 1", 2, 10.0);
        return builder;
    }

    public FilmeBuilder semEstoque() {
        filme.setEstoque(0);
        return this;
    }

    public FilmeBuilder comValor(Double preco) {
        filme.setPrecoLocacao(preco);
        return this;
    }

    public Filme agora() {
        return filme;
    }
}
