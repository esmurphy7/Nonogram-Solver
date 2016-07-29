import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Evan on 7/28/2016.
 */
public class NONO
{
    private class Run
    {
        List<Integer> elements;

        public Run()
        {
            elements = new ArrayList<Integer>();
        }

        public int[] getElementsAsArray()
        {
            int[] elemArr = new int[elements.size()];

            for(int i=0; i < elements.size(); i++)
            {
                elemArr[i] = elements.get(i);
            }

            return elemArr;
        }
    }

    public int numRows;
    public int numColumns;
    public List<Run> rowSequence;
    public List<Run> columnSequence;

    public NONO(Scanner in)
    {
        numRows = in.nextInt();
        numColumns = in.nextInt();

        // parse each line for row runs numRows times
        rowSequence = new ArrayList<Run>();
        for(int i = 0; i < numRows; i++)
        {
            // parse line for run elements "numElements" times
            int numElements = in.nextInt();
            Run run = new Run();
            for(int j=0; j < numElements; j++)
            {
                run.elements.add(in.nextInt());
            }
        }

        // parse each line for column runs numColumns times
        columnSequence = new ArrayList<Run>();
        for(int i = 0; i < numColumns; i++)
        {
            // parse line for run elements "numElements" times
            int numElements = in.nextInt();
            Run run = new Run();
            for(int j=0; j < numElements; j++)
            {
                run.elements.add(in.nextInt());
            }
        }
        
        // create an NFA from each run
        // create from row sequences
        List<NFA> NFAs = new ArrayList<NFA>();
        for(Run run : rowSequence)
        {
            NFA nfa = new NFA(run.getElementsAsArray());
            NFAs.add(nfa);
        }

        // create from column sequence
        for(Run run : columnSequence)
        {
            NFA nfa = new NFA(run.getElementsAsArray());
            NFAs.add(nfa);
        }


        // create a CNF from each NFA
//        List<CNF> CNFs = new ArrayList<CNF>();
//        for(NFA nfa : NFAs)
//        {
//            CNF cnf = new CNF(n, nfa);
//            CNFs.add(cnf);
//        }
//
//        // combine cnfs into one
//        combineCNfs(CNFs);
    }
    
    public String toString()
    {
        // return the combined CNF in dimacs format
        return "TODO: implement NONO.toString()";
    }
    
    public static void main ( String[] args )
    {
        Scanner in;

        // if a nonogram file was passed, build scanner from it
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
        // otherwise, nonogram data is streamed through stdin
        else
        {
            in = new Scanner( System.in );
        }

        // build nonogram and output combined cnf values
        NONO nonogram = new NONO(in);
        nonogram.toString();
    }
}
