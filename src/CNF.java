/**
 * Created by Evan on 7/28/2016.
 */
class CNF
{
    public String[] clauses;  // the clauses as strings, optional
    public int[][] cl; // the clauses as lists of integers
    public int C = 0; // count of clauses;
    public int V = 0; // count of variables

    CNF ( int n, NFA nfa )
    {
        // FOR YOU TO FILL IN
    }

    public static void main ( String[] args )
    {
        // This is just for your testing purposes
        int n = Integer.parseInt( args[0] );
        int runs[] = {2};
        NFA nfa = new NFA( runs );
        CNF cnf = new CNF( n, nfa );
        System.out.println( "C = "+cnf.C );
        for (int c=0; c<cnf.C; ++c) {
            for (int i=0; i<cnf.cl[c].length; ++i)
                System.out.print( cnf.cl[c][i]+" " );
            System.out.println();
        }
    }
}
