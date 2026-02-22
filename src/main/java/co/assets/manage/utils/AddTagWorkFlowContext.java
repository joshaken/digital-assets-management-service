package co.assets.manage.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class AddTagWorkFlowContext {
    boolean success;
    String failReason;
    byte[] image;
    Map<String, Double> tagsConfidenceMap;
}
