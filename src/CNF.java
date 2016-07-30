import java.util.*;

/**
 * Created by Evan on 7/28/2016.
 */
public class CNF
{
    private interface ICNFComponent
    {
        public boolean getBooleanValue();
        public boolean negate();
        public boolean isNegated();
    }

    private class kTransition implements ICNFComponent
    {
        public int k;
        public int a;
        public int q;
        private boolean negated = false;

        public kTransition(int k, int a, int q)
        {
            this.k = k;
            this.a = a;
            this.q = q;
        }

        public String toString()
        {
            String str = (negated) ? "-" : "";
            str += "t";

            str += Integer.toString(k);
            str += Integer.toString(a);
            str += Integer.toString(q);

            return str;
        }

        @Override
        public boolean getBooleanValue() {
            return false;
        }

        @Override
        public boolean negate() {
            negated = !negated;
            return negated;
        }

        @Override
        public boolean isNegated() {
            return negated;
        }
    }

    private class Literal implements ICNFComponent
    {
        public char var = 'x';
        public int i;
        private boolean negated = false;

        public Literal(int i)
        {
            this.i = i;
        }

        public String toString()
        {
            String str = (negated) ? "-" : "";
            str += var;
            str += Integer.toString(i);
            return str;
        }

        @Override
        public boolean getBooleanValue() {
            return false;
        }

        @Override
        public boolean negate() {
            negated = !negated;
            return negated;
        }

        @Override
        public boolean isNegated() {
            return negated;
        }
    }

    private class StateSequence implements ICNFComponent
    {
        public int state;
        public int k;
        private boolean negated = false;

        public StateSequence(int state, int k)
        {
            this.state = state;
            this.k = k;
        }

        public String toString()
        {
            String str = (negated) ? "-" : "";
            str += Integer.toString(state);
            str += "q";
            str += Integer.toString(k);
            return str;
        }

        @Override
        public boolean getBooleanValue() {
            return false;
        }

        @Override
        public boolean negate()
        {
            negated = !negated;
            return negated;
        }

        @Override
        public boolean isNegated() {
            return negated;
        }
    }

    private class Clause implements ICNFComponent
    {
        private boolean negated = false;

        public List<ICNFComponent> variables;

        public Clause(List<ICNFComponent> variables)
        {
            this.variables = variables;
        }

        public Clause(ICNFComponent ...variables)
        {
            this.variables = new ArrayList<ICNFComponent>();
            for(int i=0; i < variables.length; i++)
            {
                this.variables.add(variables[i]);
            }
        }

        @Override
        public boolean getBooleanValue()
        {
            // since a clause is a disjunction of ORs, return true if any of the literals is true
            for(ICNFComponent var : variables)
            {
                if(var.getBooleanValue())
                {
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean negate() {
            negated = !negated;
            return negated;
        }

        @Override
        public boolean isNegated() {
            return negated;
        }

        public String toString()
        {
            String str = "( ";
            for(ICNFComponent var : variables)
            {
                str += var.toString();

                // append an OR symbol unless it's the last variable
                str += (var == variables.get(variables.size()-1)) ? "" : " V ";
            }

            return str + " )";
        }
    }

    public List<Clause> clauses = new ArrayList<Clause>();
    public int C = 0; // count of clauses;
    public int V = 0; // count of variables

    CNF ( int n, NFA nfa )
    {
        // build clauses for each k where 1 <= k <= n
        for(int k=1; k <= n; k++)
        {
            // Step (i)
            // for each nfa transition, build two cnf clauses
            List<Clause> iClauses = new ArrayList<Clause>();
            for(NFA.Transition transition : nfa.transitions)
            {
                // build kTransition
                kTransition tkaq = new kTransition(k, transition.input, transition.dest);
                tkaq.negated = true;

                // build literal
                Literal xi = new Literal(k);
                xi.negated = (transition.input == 0);

                // build state sequence
                StateSequence stateSeq = new StateSequence(transition.dest, k);

                // build left-hand clause
                Clause leftClause = new Clause(tkaq, xi);

                // build right-hand clause
                Clause rightClause = new Clause(tkaq, stateSeq);

                // store them in list specific to this step
                iClauses.add(leftClause);
                iClauses.add(rightClause);

            }

            // Step (ii)
            // build a clause for every nfa state
            List<Clause> iiClauses = new ArrayList<Clause>();
            for(int q=0; q < nfa.Q; q++)
            {
                // create a clause
                Clause clause = new Clause();

                // first element of the clause is the negated state sequence (starting at k-1)
                StateSequence stateSeq = new StateSequence(q, k-1);
                stateSeq.negated = true;
                clause.variables.add(stateSeq);

                // create kTransition for each nfa transition
                for(NFA.Transition nfaTransition : nfa.transitions)
                {
                    // only consider transitions such that the transition's src state is equal to the state sequence's state
                    if(nfaTransition.src != stateSeq.state)
                    {
                        continue;
                    }

                    // create a kTransition such that its q value is equal to the transition's dest state
                    kTransition kTransition = new kTransition(k, nfaTransition.input, nfaTransition.dest);

                    // add the kTransition to the clause as an element
                    clause.variables.add(kTransition);
                }

                // add the clause to this step's set
                iiClauses.add(clause);
            }

            // Step (iii)
            // build a clause for every nfa state
            List<Clause> iiiClauses = new ArrayList<Clause>();
            for(int q=0; q < nfa.Q; q++)
            {
                // create a clause
                Clause clause = new Clause();

                // the first element of the clause is the negated state sequence
                StateSequence stateSeq = new StateSequence(q, k);
                stateSeq.negated = true;
                clause.variables.add(stateSeq);

                // create a kTransition for each nfa transition
                for(NFA.Transition transition : nfa.transitions)
                {
                    // only consider nfa transitions such that its dest state is equal to the state sequence's state
                    if(transition.dest != stateSeq.state)
                    {
                        continue;
                    }

                    // create a kTransition such that its q value is equal to nfa transition's dest state
                    kTransition kTransition = new kTransition(k, transition.input, transition.dest);

                    // add the transition to the clause as an element
                    clause.variables.add(kTransition);
                }

                // add the clause to this step's set
                iiiClauses.add(clause);

            }

            // Step (iv)
            // build a clause for each input type (0 and 1)
            List<Clause> ivClauses = new ArrayList<Clause>();

            // input 0
            // first element of the 0 clause is the literal
            Clause clause0 = new Clause();
            Literal xk = new Literal(k);
            clause0.variables.add(xk);

            // first element of the 1 clause is the negated literal
            Clause clause1 = new Clause();
            Literal notxk = new Literal(k);
            notxk.negate();
            clause1.variables.add(notxk);

            // create a kTransition for each nfa transition
            for(NFA.Transition transition : nfa.transitions)
            {
                // create the kTransition
                kTransition kTransition = new kTransition(k, transition.input, transition.dest);

                if(kTransition.a == 0)
                {
                    // add the kTransition to the 0 clause
                    clause0.variables.add(kTransition);
                }
                else if(kTransition.a == 1)
                {
                    // add the kTransition to the 1 clause
                    clause1.variables.add(kTransition);
                }
            }

            // add both 0 and 1 clauses to this step's set
            ivClauses.add(clause0);
            ivClauses.add(clause1);


            // Step (v)
            // build a clause for each nfa ransition
            List<Clause> vClauses = new ArrayList<Clause>();
            for(NFA.Transition transition : nfa.transitions)
            {
                // create a clause
                Clause clause = new Clause();

                // the first element of the clause is the ktransition's negation
                kTransition kTransition = new kTransition(k, transition.input, transition.dest);
                kTransition.negate();
                clause.variables.add(kTransition);

                // create a sequence state such that its state is equal to the nfa transition's src state
                StateSequence stateSeq = new StateSequence(transition.src, k - 1);
                clause.variables.add(stateSeq);

                // add the clause to this steps set
                vClauses.add(clause);
            }


            // combine set of clauses for all steps
            this.clauses.addAll(iClauses);
            this.clauses.addAll(iiClauses);
            this.clauses.addAll(iiiClauses);
            this.clauses.addAll(ivClauses);
            this.clauses.addAll(vClauses);
        }

        // Step (vi)
        List<Clause> viClauses = new ArrayList<Clause>();
        for(int q=0; q < nfa.Q; q++)
        {
            // create a clause for each state that isn't an initial state
            if(!NFA.stateIsInSet(q, nfa.I))
            {
                // create a state sequence such that its k-value is 0
                StateSequence stateSeq = new StateSequence(q, 0);
                stateSeq.negate();

                // create and add a clause for the state sequence
                Clause clause = new Clause(stateSeq);
                viClauses.add(clause);
            }

            // create a clause for each state that isn't a final state
            if(!NFA.stateIsInSet(q, nfa.F))
            {
                // create state sequence such that its k-value is n (max value)
                StateSequence stateSeq = new StateSequence(q, n);
                stateSeq.negate();

                // create and add a clause for the state sequence
                Clause clause = new Clause(stateSeq);
                viClauses.add(clause);
            }
        }
        this.clauses.addAll(viClauses);

        // store number of clauses
        this.C = clauses.size();

        // set this CNFs number of distinct vars
        this.V = getDistinctVariablesAsStrings(clauses).size();
    }

    public static List<String> getDistinctVariablesAsStrings(List<Clause> ...clauseSets)
    {
        // create set of distinct variables for this entire CNF
        List<String> distinctVariables = new ArrayList<String>();

        // search every variable of every clause and check if it's been accounted for
        for(List<Clause> clauses : clauseSets)
        {
            for(Clause clause : clauses)
            {
                for (ICNFComponent var : clause.variables)
                {
                    // a negated variable doesn't necessarily make it distinct
                    boolean varWasNegated = false;
                    if (var.isNegated())
                    {
                        var.negate();
                        varWasNegated = true;
                    }

                    // only mark the variable as distinct if it hasn't been accounted for yet
                    if (!distinctVariables.contains(var.toString()))
                    {
                        distinctVariables.add(var.toString());
                    }

                    // negate it back
                    if(varWasNegated)
                    {
                        var.negate();
                    }
                }
            }
        }

        return distinctVariables;
    }

    public String toString()
    {
        String andStr = " ^ ";
        String newline = "\n";
        String clausesStr = "";

        for(Clause clause : clauses)
        {
            clausesStr += clause.toString();
            clausesStr += (clause == clauses.get(clauses.size()-1)) ? "" : andStr + newline;

        }

        return clausesStr;
    }


    public static void main ( String[] args )
    {
        // This is just for your testing purposes
        int n = Integer.parseInt( args[0] );
        int runs[] = {2};
        NFA nfa = new NFA( runs );
        CNF cnf = new CNF( n, nfa );

        System.out.println("CNF: ");
        System.out.println("V: " + cnf.V);
        System.out.println("C: " + cnf.C);
        System.out.println(cnf.toString());

    }
}
