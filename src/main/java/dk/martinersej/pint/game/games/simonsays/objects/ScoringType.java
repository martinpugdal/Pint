package dk.martinersej.pint.game.games.simonsays.objects;

public enum ScoringType {

    PLACEMENT(4, 3, 2, 1),
    PARTICIPATION(1);

    private final int[] pointsToGive;
    ScoringType(int firstPlace, int... otherPlaces) {
        this.pointsToGive = new int[otherPlaces.length + 1];
        pointsToGive[0] = firstPlace;
        System.arraycopy(otherPlaces, 0, pointsToGive, 1, otherPlaces.length);
    }

    public int getPoints(int placement) {
        if (this == PLACEMENT) {
            return getPlacementPoints(placement);
        }
        return pointsToGive[0];
    }

    private int getPlacementPoints(int placement) {
        if (placement > pointsToGive.length) {
            return PARTICIPATION.getPoints(0);
        }
        return pointsToGive[placement - 1];
    }
}
