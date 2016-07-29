import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Evan on 7/28/2016.
 */
public class NFA
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
        if(runs.length <= 0)
        {
            System.out.println("Cannot create NFA from empty run");
            return;
        }

        // convert the run to a regular expression (always begins with "0*")
        String regex = "0*";

        // for each element in the run
        for(int i=0; i < runs.length; i++)
        {
            // create string of 1's of size "element"
            int element = runs[i];
            for(int j=0; j < element; j++)
            {
                regex += "1";
            }

            // each string of 1's is always followed by a "0+" unless its the last, then "0*"
            regex += (i + 1 == runs.length) ? "0*" : "0+";
        }


        // build the nfa from the regular expression
        // calculate the number of states
        Q = runs.length;
        for(int i=0; i < runs.length; i++)
        {
            Q += runs[i];
        }

        // parse the regular expression to create transitions
        transitions = new ArrayList<Transition>();
        int currentState = 0;

        // convert regular expression to character array
        char[] characters = new char[regex.length()];
        regex.getChars(0, regex.length(), characters, 0);
        for(int i=0; i < characters.length; i++)
        {
            // build transition for 0 input
            if(characters[i] == '0')
            {
                if(i + 1 < characters.length)
                {
                    if(characters[i+1] == '*')
                    {
                        // create new transition to same state
                        transitions.add(new Transition(currentState, 0, currentState));
                    }
                    else if(characters[i+1] == '+')
                    {
                        // create new transition to next state (and move to next state)
                        transitions.add(new Transition(currentState, 0, ++currentState));

                        // create another transition to the same state
                        transitions.add(new Transition(currentState, 0, currentState));
                    }
                }
            }
            // build transition for 1 input
            else if(characters[i] == '1')
            {
                // build a new transition to next state
                transitions.add(new Transition(currentState, 1, ++currentState));
            }

            // check that current state is valid
            if(currentState > Q)
            {
                System.out.println(String.format("Cannot build NFA from regex, current state (%d) is out of range (%d)", currentState, Q));
                System.out.println("Regex: " + regex);
                return;
            }
        }

        // set other member attributes
        I = new int[1];
        I[0] = 0;

        F = new int[1];
        F[0] = Q - 1;

        T = transitions.size();
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
        int[] runs = {2, 1, 3};
        NFA nfa = new NFA( runs );
        System.out.println( nfa );

        NFA nfas = new NFA( in );
        System.out.println( nfas );
    }
}
