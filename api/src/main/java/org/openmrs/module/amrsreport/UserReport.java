package org.openmrs.module.amrsreport;
import org.openmrs.BaseOpenmrsObject;

import org.openmrs.User;

public class UserReport extends BaseOpenmrsObject{

private  User amrsReportsUser;
private String  reporDefinitionUuid;
private Integer id;
	public Integer getId() {
		// TODO Auto-generated method stub
		return id;
	}

	public void setId(Integer id) {
		// TODO Auto-generated method stub
		this.id=id;
		
	}
	
	
		public User   getAmrsReportsUser() {
		// TODO Auto-generated method stub
		return  amrsReportsUser;
	    }
		public  void   setAmrsReportsUser (User  amrsReportsUser ) {
		
		// TODO Auto-generated method stub
		 this.amrsReportsUser=amrsReportsUser;
		 
		 
	    }
		public String   getReporDefinitionUuid() {
		
		// TODO Auto-generated method stub
		return  reporDefinitionUuid;
	    }
		public  void   setReporDefinitionUuid (String  reporDefinitionUuid ) {
		
		// TODO Auto-generated method stub by system
		 this.reporDefinitionUuid=reporDefinitionUuid;
		}
}