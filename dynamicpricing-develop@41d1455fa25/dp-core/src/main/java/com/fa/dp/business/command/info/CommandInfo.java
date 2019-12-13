package com.fa.dp.business.command.info;

import java.io.Serializable;

import lombok.Data;

@Data
public class CommandInfo implements Serializable {

	private static final long serialVersionUID = 1881599609531563247L;

	private String id;

	private String name;

	private int executionSequence;

	private String description;

	private String process;

	private boolean active;

}
