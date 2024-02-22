package dk.martinersej.pint.game.games.simonsays.objects;

public enum ScoringType {

    PLACEMENT(3, 2, 1),
    PARTICIPATION(1);

    private final int[] pointsToGive;

    ScoringType(int... pointsToGive) {
        this.pointsToGive = pointsToGive;
    }

    public int getPoints(int placement) {
        if (this == PLACEMENT) {
            return getPlacementPoints(placement);
        }
        return pointsToGive[0];
    }

    private int getPlacementPoints(int placement) {
        if (placement > pointsToGive.length) {
            return 0;
        }
        return pointsToGive[placement - 1];
    }
}
