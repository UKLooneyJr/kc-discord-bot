package com.kelvinconnect.discord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VotingBooth {

    private static class Candidate {
        Candidate(String name) {
            this.name = name;
            this.count = 0;
        }

        final String name;
        int count;
    }

    private static class Voter {
        Voter(String id) {
            this.id = id;
            this.candidate = null;
        }

        final String id;
        Candidate candidate;
    }

    private static class CandidateComparator implements Comparator<Candidate> {
        @Override
        public int compare(Candidate o1, Candidate o2) {
            return Integer.compare(o2.count, o1.count);
        }
    }

    private final List<Candidate> candidates;
    private final List<Voter> voters;

    public VotingBooth() {
        candidates = new ArrayList<>();
        voters = new ArrayList<>();
    }

    /**
     * @return False if this is the voter's first vote. True if they have changed vote.
     */
    public boolean vote(String name, String voterId) {
        Candidate candidate = getCandidate(name);
        Voter voter = getVoter(voterId);
        candidate.count++;
        if (voter.candidate == null) {
            voter.candidate = candidate;
            return false;
        } else {
            Candidate previousCandidate = voter.candidate;
            previousCandidate.count--;
            if (previousCandidate.count == 0) {
                candidates.remove(previousCandidate);
            }
            voter.candidate = candidate;
            return true;
        }
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

    private Candidate getCandidate(String name) {
        for (Candidate c : candidates) {
            if (c.name.equals(name)) {
                return c;
            }
        }
        Candidate candidate = new Candidate(name);
        candidates.add(candidate);
        return candidate;
    }

    private Voter getVoter(String id) {
        for (Voter v : voters) {
            if (v.id.equals(id)) {
                return v;
            }
        }
        Voter voter = new Voter(id);
        voters.add(voter);
        return voter;
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
