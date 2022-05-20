package me.superbiebel.referee_planner.domain;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
public class Availability {
    @Getter
    private Location startLocation;
    
    @Getter
    private boolean endLocationEnabled;
    @Getter
    private Location endLocation;
    
    @Getter
    private TimePeriod timePeriod;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
    
        Availability that = (Availability) o;
    
        if (isEndLocationEnabled() != that.isEndLocationEnabled()) return false;
        if (!getStartLocation().equals(that.getStartLocation())) return false;
        if (getEndLocation() != null ? !getEndLocation().equals(that.getEndLocation()) : that.getEndLocation() != null)
            return false;
        return getTimePeriod().equals(that.getTimePeriod());
    }
    
    @Override
    public int hashCode() {
        int result = getStartLocation().hashCode();
        result = 31 * result + (isEndLocationEnabled() ? 1 : 0);
        result = 31 * result + (getEndLocation() != null ? getEndLocation().hashCode() : 0);
        result = 31 * result + getTimePeriod().hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Availability{");
        sb.append("startLocation=").append(startLocation);
        sb.append(", endLocationEnabled=").append(endLocationEnabled);
        if (endLocationEnabled) {
            sb.append(", endLocation=").append(endLocation);
        }
        sb.append(", timePeriod=").append(timePeriod);
        sb.append('}');
        return sb.toString();
    }
}
