package core.observations;

import core.AbstractGameState;
import core.interfaces.IObservation;
import core.interfaces.IPrintable;

import java.util.Arrays;

public class VectorObservation<T> implements IObservation, IPrintable {

    private T[] values;

    public VectorObservation(T[] arrayValues){
        this.values = arrayValues;
    }

    @Override
    public void printToConsole(AbstractGameState gameState) {
        System.out.println(Arrays.toString(values));
    }

    @Override
    public IObservation copy() {
        return null;
    }

    @Override
    public IObservation next(AbstractAction action) {
        return null;
    }
}
