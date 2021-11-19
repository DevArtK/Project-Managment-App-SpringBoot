package cooksys.projectmanagementapp.projectmanagement.services.impl;

import cooksys.projectmanagementapp.projectmanagement.controllers.CompanyController;
import cooksys.projectmanagementapp.projectmanagement.dtos.CompanyDto;
import cooksys.projectmanagementapp.projectmanagement.dtos.CompanyRequestDto;
import cooksys.projectmanagementapp.projectmanagement.dtos.TeamDto;
import cooksys.projectmanagementapp.projectmanagement.dtos.TeamRequestDto;
import cooksys.projectmanagementapp.projectmanagement.entities.Team;
import cooksys.projectmanagementapp.projectmanagement.exceptions.BadRequestException;
import cooksys.projectmanagementapp.projectmanagement.exceptions.NotFoundException;
import cooksys.projectmanagementapp.projectmanagement.mappers.TeamMapper;
import cooksys.projectmanagementapp.projectmanagement.repositories.TeamRepository;
import cooksys.projectmanagementapp.projectmanagement.services.AuthService;
import cooksys.projectmanagementapp.projectmanagement.services.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamMapper teamMapper;
    private final TeamRepository teamRepository;
    private final AuthService authService;



    @Override
    public TeamDto updateTeam(long id, TeamRequestDto teamDto) {

        final var user = authService.authenticate(teamDto.getCredentials());

        if (user.isAdmin() && user.getTeam().getId().equals(id)) {

            var userTeam =
                    teamRepository.findById(user.getTeam().getId()).orElseThrow(() -> new NotFoundException("Company Not Found"));


            userTeam.setName(teamDto.getTeam().getName());
            userTeam.setDescription(teamDto.getTeam().getDescription());

            teamRepository.saveAndFlush(userTeam);

            return teamMapper.entityToResponseDto(userTeam);
        }

        throw new BadRequestException("User doesn't belong to this Team or not an Admin");
    }

    @Override
    public List<TeamDto> getAdminTeams() {
        return teamMapper.entitiesToResponseDtos(teamRepository.findAll());
    }

    @Override
    public TeamDto getAdminTeamById(long id) {
        return teamMapper.entityToResponseDto(teamRepository.getById(id));
    }
}
