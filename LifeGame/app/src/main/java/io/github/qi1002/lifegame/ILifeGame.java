package io.github.qi1002.lifegame;

import android.util.Pair;

import java.util.HashSet;

public interface ILifeGame {
    public Pair<HashSet<Integer>, HashSet<Integer>> getNewResult();
    public void add(int y, int x);
}
