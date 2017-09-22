package com.kelvinconnect.discord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VotingBooth {

    private static class Candidate {
        String name;
        int count;
    }

    private static class CandidateComparator implements Comparator<Candidate> {
        @Override
        public int compare(Candidate o1, Candidate o2) {
            return Integer.compare(o2.count, o1.count);
        }
    }

    private List<Candidate> candidates;

    public VotingBooth() {
        candidates = new ArrayList<>();
    }

    public void vote(String name) {
        Candidate candidate = findCandidate(name);
        if (candidate == null) {
            candidate = new Candidate();
            candidate.name = name;
            candidate.count = 0;
            candidates.add(candidate);
        }
        candidate.count++;
    }

    public String getResults() {
        if (candidates.isEmpty()) {
            return "Nobody has voted yet.";
        }
        candidates.sort(new CandidateComparator());
        StringBuilder sb = new StringBuilder();
        for (Candidate c : candidates) {
            sb.append(c.name);
            sb.append(" has ");
            sb.append(c.count);
            sb.append(c.count == 1 ? " vote" : " votes");
            sb.append("\n");
        }
        return sb.toString();
    }

    public boolean hasVotes() {
        return !candidates.isEmpty();
    }

    private Candidate findCandidate(String name) {
        for (Candidate c : candidates) {
            if (c.name.equals(name)) {
                return c;
            }
        }
        return null;
    }

    public String getWinner() {
        if (candidates.isEmpty()) {
            return "Nobody";
        }
        candidates.sort(new CandidateComparator());
        return candidates.get(0).name;
    }

    public void reset() {
        candidates.clear();
    }

}
