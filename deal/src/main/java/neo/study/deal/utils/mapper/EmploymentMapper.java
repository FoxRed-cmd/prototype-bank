package neo.study.deal.utils.mapper;

import java.util.UUID;

import neo.study.deal.dto.EmploymentDto;
import neo.study.deal.entity.Employment;

public class EmploymentMapper {
    public static Employment toEntity(EmploymentDto employmentDto) {
        return Employment.builder()
                .id(UUID.randomUUID())
                .status(employmentDto.getEmploymentStatus())
                .employerINN(employmentDto.getEmployerINN())
                .salary(employmentDto.getSalary())
                .position(employmentDto.getPosition())
                .workExperienceTotal(employmentDto.getWorkExperienceTotal())
                .workExperienceCurrent(employmentDto.getWorkExperienceCurrent())
                .build();
    }
}
