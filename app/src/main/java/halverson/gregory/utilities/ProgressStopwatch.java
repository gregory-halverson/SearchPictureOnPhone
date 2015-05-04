package halverson.gregory.utilities;

/**
 * Created by Gregory on 5/4/2015.
 */
public class ProgressStopwatch
{
    private long startTimeNano;

    public ProgressStopwatch()
    {
        this.startTimeNano = System.nanoTime();
    }

    public float estimateSeconds(int operationsCompleted, int totalOperations)
    {
        return (System.nanoTime() - startTimeNano) / (float)operationsCompleted / 1000000000.0f * (totalOperations - operationsCompleted);
    }

    public String estimateMinutesSeconds(int operationsCompleted, int totalOperations)
    {
        int secondsRemaining = (int) estimateSeconds(operationsCompleted, totalOperations);

        return String.format("%02d", secondsRemaining / 60) + ":" + String.format("%02d", secondsRemaining % 60);
    }
}
