package me.superbiebel.referee_planner.domain;

import lombok.Builder;
import lombok.Getter;
import org.optaplanner.core.api.domain.lookup.PlanningId;

import java.util.UUID;

@Builder(toBuilder = true)
public class Availability {
    @PlanningId
    @Getter
    private UUID availabilityUUID;
    @Getter
    private Location startLocation;
    @Getter
    private boolean endLocationEnabled;
    @Getter
    private Location endLocation;
    @Getter
    private TimePeriod timePeriod;
    @Getter
    private boolean maxRangeEnabled;
    @Getter
    private long maxRange;
    
    //
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Availability that = (Availability) o;
        
        if (isEndLocationEnabled() != that.isEndLocationEnabled()) return false;
        if (isMaxRangeEnabled() != that.isMaxRangeEnabled()) return false;
        if (isMaxRangeEnabled() && getMaxRange() != that.getMaxRange()) return false;
        if (!getStartLocation().equals(that.getStartLocation())) return false;
        if (endLocationEnabled && getEndLocation() != null ? !getEndLocation().equals(that.getEndLocation()) : that.getEndLocation() != null)
            return false;
        return getTimePeriod().equals(that.getTimePeriod());
    }
    
    @Override
    public int hashCode() {
        int result = getStartLocation().hashCode();
        result = 31 * result + (isEndLocationEnabled() ? 1 : 0);
        if (endLocationEnabled) {
            result = 31 * result + (getEndLocation() != null ? getEndLocation().hashCode() : 0);
        }
        result = 31 * result + getTimePeriod().hashCode();
        result = 31 * result + (isMaxRangeEnabled() ? 1 : 0);
        if (maxRangeEnabled) {
            result = 31 * result + (int) (getMaxRange() ^ (getMaxRange() >>> 32));
        }
        return result;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Availability{");
        sb.append("startLocation=").append(startLocation);
        sb.append(", endLocationEnabled=").append(endLocationEnabled);
        sb.append(", endLocation=").append(endLocation);
        sb.append(", timePeriod=").append(timePeriod);
        sb.append(", maxRangeEnabled=").append(maxRangeEnabled);
        sb.append(", maxRange=").append(maxRange);
        sb.append('}');
        return sb.toString();
    }
}
