package com.fa.dp.business.command.mapper;

import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import org.mapstruct.Mapper;

/**
 * @author misprakh
 */
@Mapper(componentModel = "spring")
public interface CommandMapper {

	CommandInfo mapDomainToInfo(Command command);

	Command mapInfoToDomain(CommandInfo commandInfo);
}
