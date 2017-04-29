import java.util.function.Predicate;

public class PredicateFactory {

    public Predicate<String> processaCondicao(String condicao) {

        String[] operadores = {"&", "OR"};

        Predicate<String> anterior = null;
        Predicate<String> atual = null;

        for (String operador : operadores) {

            if (condicao.contains(operador)) {

                String[] subCondicoes = condicao.split(operador);

                for (String subCondicao : subCondicoes) {

                    if (subCondicao.isEmpty()) {
                        continue;
                    }

                    anterior = atual;
                    atual = this.processaCondicao(subCondicao);

                    if ((anterior == null && !operador.equals("!"))) {
                        continue;
                    }

                    switch (operador) {

                        case "&":

                            atual = anterior.and(atual);
                            break;

                        case "OR":

                            atual = anterior.or(atual);
                            break;

                    }


                }
            }

        }

        if (atual != null) {
            return atual;
        }

        String[] operadores2 = {"+", "-", "!", "="};

        String c = String.valueOf(condicao.charAt(0));
        for (String operador : operadores2) {

            if (c.equals(operador)) {

                switch (operador) {

                    case  "=" :
                        return new RegraIgualdade(condicao.substring(1));

                    case "+" :
                        return new RegraMaior(condicao.substring(1));

                    case "-":
                        return new RegraMenor(condicao.substring(1));

                    case "!":
                        return this.processaCondicao(condicao.substring(1)).negate();

                }
            }

        }

        return new RegraContem(condicao);
    }

    public static void main(String[] args){
        System.out.println(new PredicateFactory().processaCondicao("+1&!+3").test("4"));
    }

}

abstract class Regra implements Predicate<String> {

    protected String condicao;

    public Regra(String condicao) {
        this.condicao = condicao;
    }
}

class RegraIgualdade extends Regra {

    public RegraIgualdade(String condicao) {
        super(condicao);
    }

    public boolean test(String s) {
        return s.equals(condicao);
    }

}

class RegraContem extends Regra {

    public RegraContem(String condicao) {
        super(condicao);
    }

    @Override
    public boolean test(String s) {
        return s.contains(condicao);
    }

}

class RegraMaior extends Regra {

    public RegraMaior(String condicao) {
        super(condicao);
    }

    @Override
    public boolean test(String s) {
        return s.compareTo(this.condicao) > 0;
    }

}

class RegraMenor extends Regra {

    public RegraMenor(String condicao) {
        super(condicao);
    }

    @Override
    public boolean test(String s) {
        return s.compareTo(this.condicao) < 0;
    }

}