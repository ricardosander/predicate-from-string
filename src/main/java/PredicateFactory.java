import java.util.function.Predicate;

class StringPredicateFactory extends PredicateFactory<String> {

    @Override
    public String converter(String s) {
        return s;
    }
}

class InteiroPredicateFactory extends PredicateFactory<Integer> {

    @Override
    public Integer converter(String s) {
        return Integer.parseInt(s);
    }
}

public abstract class PredicateFactory<T extends Comparable<T>> {

    public abstract T converter(String s);

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

        return new RegraIgualdade<>(condicao);
    }

    public static void main(String[] args){
        System.out.println(new StringPredicateFactory().processaCondicao("+1&!+3").test("2"));
        System.out.println(new InteiroPredicateFactory().processaCondicao("+1&!+3").test(String.valueOf(2)));
    }

}

abstract class Regra<T extends Comparable<T>> implements Predicate<T> {

    protected T condicao;

    public Regra(T condicao) {
        this.condicao = condicao;
    }
}

class RegraIgualdade<T extends Comparable<T>> extends Regra<T> {

    public RegraIgualdade(T condicao) {
        super(condicao);
    }

    public boolean test(T s) {
        return s.equals(condicao);
    }

}

//class RegraContem<T extends Comparable<T>> extends Regra<T> {
//
//    public RegraContem(T condicao) {
//        super(condicao);
//    }
//
//    @Override
//    public boolean test(T s) {
//        return s.contains(condicao);
//    }
//
//}

class RegraMaior<T extends Comparable<T>> extends Regra<T> {

    public RegraMaior(T condicao) {
        super(condicao);
    }

    @Override
    public boolean test(T s) {
        return s.compareTo(this.condicao) > 0;
    }

}

class RegraMenor<T extends Comparable<T>> extends Regra<T> {

    public RegraMenor(T condicao) {
        super(condicao);
    }

    @Override
    public boolean test(T s) {
        return s.compareTo(this.condicao) < 0;
    }

}