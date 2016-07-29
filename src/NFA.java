import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Evan on 7/28/2016.
 */
class NFA
{
    private class Transition
    {
        public int src;
        public int input;
        public int dest;

        public Transition(int src, int input, int dest)
        {
            this.src = src;
            this.input = input;
            this.dest = dest;
        }

        public String toString()
        {
            String srcStr = Integer.toString(src);
            String inputStr = Integer.toString(input);
            String destStr = Integer.toString(dest);

            return "(" + srcStr + ", " + inputStr + ", " + destStr + ")";
        }
    }

    int Q;                          // number of states
    int T;                          // number of transitions
    int I[];                        // initial states
    int F[];                        // final (=output) states
    List<Transition> transitions;   // the transitions

    NFA( int[] runs )
    {
        // FOR YOU TO FILL IN
    }

    NFA( Scanner in )
    {
        Q = in.nextInt();
        T = in.nextInt();

        int s = in.nextInt();
        I = new int[s];
        for (int i=0; i<s; ++i)
        {
            I[i] = in.nextInt();
        }

        s = in.nextInt();
        F = new int[s];
        for (int i=0; i<s; ++i)
        {
            F[i] = in.nextInt();
        }

        transitions = new ArrayList<Transition>();
        for (int i=0; i<T; ++i)
        {
            Transition transition = new Transition(in.nextInt(), in.nextInt(), in.nextInt());
            transitions.add(transition);
        }
    }

    public String toString()
    {
        // add a string for each member of the NFA class to a list to return
        String memberString = "";
        String newline = "\n";

        // number of states
        memberString += String.format("Q: %d", Q) + newline;

        // number of transitions
        memberString += String.format("T: %d", T) + newline;

        // initial states
        String initStates = "Initial States: ";
        for(int i=0; i < I.length; i++)
        {
            initStates += I[i] + ", ";
        }
        memberString += initStates + newline;

        // final states
        String finalStates = "Final States: ";
        for(int i=0; i < F.length; i++)
        {
            finalStates += F[i] + ", ";
        }
        memberString += finalStates + newline;

        // transitions
        String transitionStr = "Transitions: " + newline;
        for(Transition transition : transitions)
        {
            transitionStr += transition.toString() + newline;
        }
        memberString += transitionStr + newline;

        return memberString;
    }

    public static void main ( String[] args )
    {
        Scanner in;

        // if an nfa file was passed, build scanner from it
        if(args.length == 1)
        {
            ClassLoader classLoader = NFA.class.getClassLoader();
            File file = new File(classLoader.getResource(args[0]).getFile());
            try
            {
                in = new Scanner(file);
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
                return;
            }
        }
        // otherwise, nfa data is streamed through stdin
        else
        {
            in = new Scanner( System.in );
        }

        // This is just for your testing purposes
        //int[] runs = {2};
        //NFA nfa = new NFA( runs );
        //System.out.println( nfa );

        NFA nfas = new NFA( in );
        System.out.println( nfas );

    }

}
