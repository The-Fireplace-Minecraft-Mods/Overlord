package dev.the_fireplace.overlord.domain.data;

import java.util.UUID;
import java.util.stream.Stream;

public interface PlayerAlliances
{
    /**
     * @return true if both players have agreed to be in an alliance, false otherwise.
     */
    boolean hasAllianceWith(UUID playerId, UUID otherPlayerId);

    /**
     * @return true if <code>playerId</code> has requested an alliance with <code>otherPlayerId</code> which has not yet been accepted, false otherwise.
     */
    boolean hasRequestedAllianceWith(UUID playerId, UUID otherPlayerId);

    /**
     * @return true if <code>playerId</code> has declared <code>otherPlayerId</code> as an enemy, false otherwise.
     */
    boolean hasDeclaredEnemy(UUID playerId, UUID otherPlayerId);

    /**
     * @return true if either player has declared the other as an enemy, false otherwise.
     */
    boolean enemyRelationExistsBetween(UUID playerId, UUID otherPlayerId);

    /**
     * Submit that <code>playerId</code> has requested <code>otherPlayerId</code> as an ally.
     * @return true if the request was successfully submitted,
     * false if the request already exists or both players have already agreed to be allies.
     */
    boolean requestAlliance(UUID playerId, UUID otherPlayerId);

    /**
     * Submit that <code>playerId</code> has denied <code>otherPlayerId</code> as an ally.
     * @return true if the denial was successfully submitted,
     * false if <code>otherPlayerId</code> has not requested <code>playerId</code> as an ally.
     */
    boolean denyAlliance(UUID playerId, UUID otherPlayerId);

    /**
     * Submit that <code>playerId</code> has declared <code>otherPlayerId</code> as an enemy.
     * @return true if <code>otherPlayerId</code> was not previously declared as an enemy,
     * false otherwise.
     */
    boolean declareEnemy(UUID playerId, UUID otherPlayerId);

    /**
     * Remove the alliance or alliance request between <code>playerId</code> and <code>otherPlayerId</code>.
     * @return true if the alliance was successfully removed,
     * false if <code>playerId</code> has not requested or entered an alliance with <code>otherPlayerId</code>.
     */
    boolean removeAlliance(UUID playerId, UUID otherPlayerId);

    /**
     * Remove <code>otherPlayerId</code> as an enemy of <code>playerId</code>.
     * @return true if <code>otherPlayerId</code> was previously declared as an enemy of <code>playerId</code>,
     * false otherwise.
     */
    boolean removeEnemy(UUID playerId, UUID otherPlayerId);

    /**
     * @return a stream of enemies that <code>playerId</code> has declared.
     */
    Stream<UUID> getEnemiesDeclaredBy(UUID playerId);

    /**
     * @return a stream of allies that <code>playerId</code> has requested, which have not yet accepted or denied the alliance.
     */
    Stream<UUID> getAlliesRequestedBy(UUID playerId);

    /**
     * @return a stream of confirmed allies of <code>playerId</code>.
     */
    Stream<UUID> getConfirmedAlliancesWith(UUID playerId);
}
