package org.lukawska.trainsmart.trainingplan.application.dto.request;

public record TrainingPlanFilterRequest(Integer pageNumber,
                                        Integer pageSize,
                                        String sort) {

    public Integer pageNumber() {
        return pageNumber != null ? pageNumber : 0;
    }

    public Integer pageSize() {
        return pageSize != null ? pageSize : 10;
    }

    public String sort() {
        return sort != null ? sort : "id";
    }
}
