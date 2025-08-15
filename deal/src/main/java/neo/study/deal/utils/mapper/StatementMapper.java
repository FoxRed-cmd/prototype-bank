package neo.study.deal.utils.mapper;

import neo.study.deal.dto.StatementDto;
import neo.study.deal.entity.Statement;

public class StatementMapper {
    public static StatementDto toDto(Statement statement) {
        var statementDto = new StatementDto();

        statementDto.setId(statement.getId());
        statementDto.setStatus(statement.getStatus());
        statementDto.setCreationDate(statement.getCreationDate());
        statementDto.setAppliedOffer(statement.getAppliedOffer());
        statementDto.setSignDate(statement.getSignDate());
        statementDto.setStatusHistory(statement.getStatusHistory());

        return statementDto;
    }
}
