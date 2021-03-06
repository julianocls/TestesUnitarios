package br.ce.wcaquino.servicos;

import br.ce.wcaquino.servicos.exception.NaoPodeDividirPorZeroException;

public class Calculadora {

    public int somar(int a, int b) {
        return a + b;
    }

    public int subtrair(int a, int b) {
        return a - b;
    }

    public int dividir(int a, int b) throws NaoPodeDividirPorZeroException {
        if (b == 0) {
            throw new NaoPodeDividirPorZeroException("Não pode dividir por zero!");
        }

        return a / b;
    }

    public void imprime() {
        System.out.println("Call of method imprime");
    }
}
