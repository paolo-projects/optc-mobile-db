package it.instruman.treasurecruisedatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paolo on 01/11/2016.
 */

public class CharacterEvolutions {
    private Integer EvolutionCharacter = null;
    private List<Integer> Evolvers = new ArrayList<>();

    public CharacterEvolutions(Integer evolutionCharacter, List<Integer> evolvers) {
        EvolutionCharacter = evolutionCharacter;
        Evolvers = evolvers;
    }

    public CharacterEvolutions() {

    }

    public Integer getEvolutionCharacter() {
        return EvolutionCharacter;
    }

    public void setEvolutionCharacter(Integer evolutionCharacter) {
        EvolutionCharacter = evolutionCharacter;
    }

    public List<Integer> getEvolvers() {
        return Evolvers;
    }

    public void setEvolvers(List<Integer> evolvers) {
        Evolvers = evolvers;
    }

    public void addEvolver(Integer value) {
        Evolvers.add(value);
    }

    public Integer getEvolver(int position) {
        return Evolvers.get(position);
    }
}
