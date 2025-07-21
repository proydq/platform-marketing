package com.platform.marketing.module.role.service.impl;

import com.platform.marketing.module.role.dto.RoleQueryDTO;
import com.platform.marketing.module.role.dto.RoleSaveDTO;
import com.platform.marketing.module.role.dto.RoleVO;
import com.platform.marketing.module.role.entity.Role;
import com.platform.marketing.module.role.repository.RoleRepository;
import com.platform.marketing.module.role.service.RoleService;
import com.platform.marketing.module.permission.entity.Permission;
import com.platform.marketing.module.permission.repository.PermissionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of role service.
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Page<RoleVO> fetchRoles(RoleQueryDTO queryDTO) {
        PageRequest page = PageRequest.of(queryDTO.getPage(), queryDTO.getSize());
        Page<Role> roles = roleRepository.findAllRoles(page);
        List<RoleVO> voList = roles.stream().map(this::toVO).collect(Collectors.toList());
        return new PageImpl<>(voList, page, roles.getTotalElements());
    }

    @Override
    public RoleVO getRole(UUID id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        return toVO(role);
    }

    @Override
    public RoleVO createRole(RoleSaveDTO saveDTO) {
        Role role = new Role();
        BeanUtils.copyProperties(saveDTO, role);
        roleRepository.save(role);
        return toVO(role);
    }

    @Override
    public RoleVO updateRole(RoleSaveDTO saveDTO) {
        Role role = roleRepository.findById(saveDTO.getId()).orElseThrow(() -> new RuntimeException("Role not found"));
        if (saveDTO.getName() != null) role.setName(saveDTO.getName());
        if (saveDTO.getCode() != null) role.setCode(saveDTO.getCode());
        if (saveDTO.getDescription() != null) role.setDescription(saveDTO.getDescription());
        if (saveDTO.getStatus() != null) role.setStatus(saveDTO.getStatus());
        return toVO(roleRepository.save(role));
    }

    @Override
    public void deleteRole(UUID id) {
        roleRepository.deleteById(id);
    }

    @Override
    public void bindPermissions(UUID id, List<UUID> permissionIds) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        Set<Permission> perms = new HashSet<>(permissionRepository.findAllById(permissionIds));
        role.setPermissions(perms);
        roleRepository.save(role);
    }

    @Override
    public List<UUID> fetchRolePermissions(UUID id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        return role.getPermissions().stream().map(Permission::getId).collect(Collectors.toList());
    }

    private RoleVO toVO(Role role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        Set<UUID> pids = role.getPermissions().stream().map(Permission::getId).collect(Collectors.toSet());
        vo.setPermissionIds(pids);
        return vo;
    }
}
