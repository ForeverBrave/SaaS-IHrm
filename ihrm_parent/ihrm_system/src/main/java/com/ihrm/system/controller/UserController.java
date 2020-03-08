package com.ihrm.system.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.domain.system.User;
import com.ihrm.domain.system.response.ProfileResult;
import com.ihrm.domain.system.response.UserResult;
import com.ihrm.domain.system.response.UserSimpleResult;
import com.ihrm.system.client.DepartmentFeignClient;
import com.ihrm.system.service.UserService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/2 15:41
 */
@RestController
@RequestMapping("/sys")
@CrossOrigin
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    /**
     * 分配角色
     * @param map
     * @return
     */
    @PutMapping("/user/assignRoles")
    public Result assignRoles(@RequestBody Map<String,Object> map){
        try {
            //1、获取被分配的用户id
            String userId = (String) map.get("id");
            //2、获取到角色的id列表
            List<String> roleIds = (List<String>) map.get("roleIds");
            //3、调用service完成角色分配
            userService.assignRoles(userId,roleIds);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return Result.SUCCESS();
    }

    /**
     * 新增用户
     * @param user
     * @return
     */
    @PostMapping("/user")
    public Result saveUser(@RequestBody User user){
        //1.设置保存的企业id(暂时用固定值 1 ，后面会解决)
        try {
            user.setCompanyId(companyId);
            user.setCompanyName(companyName);
            userService.saveUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return Result.SUCCESS();
    }

    /**
     * 分页查询所有用户
     * 指定企业id
     * @return
     */
    @GetMapping("/user")
    public Result findAllUsersByPage(int page, int size, @RequestParam Map map){
        PageResult pageResult = null;
        try {
            map.put("companyId",companyId);
            //调用service
            Page<User> allUsersByPage = userService.findAllUsersByPage(map, page, size);
            //构造返回结果  getTotalElements()：总条数  getContent()：List集合
            pageResult = new PageResult(allUsersByPage.getTotalElements(),allUsersByPage.getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(ResultCode.SUCCESS,pageResult);
    }

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    @GetMapping("/user/{id}")
    public Result findUserById(@PathVariable String id){
        UserResult userResult = null;
        try {
            //添加roleIds（用户所具有的角色）
            User user = userService.findUserById(id);
            userResult = new UserResult(user);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return new Result(ResultCode.SUCCESS,userResult);
    }

    /**
     * 查询所有用户，返回相应数据
     * @return
     * @throws Exception
     */
    @GetMapping("/user/simple")
    public Result simple(){
        List<UserSimpleResult> list = null;
        try {
            list = new ArrayList<>();
            List<User> users = userService.findAll(companyId);
            for (User user : users) {
                list.add(new UserSimpleResult(user.getId(),user.getUsername()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return new Result(ResultCode.SUCCESS,list);
    }

    /**
     * 修改用户
     * @param id
     * @param user
     * @return
     */
    @PutMapping("/user/{id}")
    public Result updateUser(@PathVariable String id,@RequestBody User user){
        try {
            //1.设置修改用户的id
            user.setId(id);
            //2.调用service进行更新
            userService.updateUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return Result.SUCCESS();
    }

    /**
     * 根据id删除用户
     * @param id
     * @return
     */
    @RequiresPermissions(value = "API-USER-DELETE")
    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE,name = "API-USER-DELETE")
    public Result deleteUserById(@PathVariable String id){
        try {
            userService.deleteUserById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return Result.SUCCESS();
    }

    /**
     * 用户登录
     * @param loginMap
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String,String> loginMap){
        String mobile = loginMap.get("mobile");
        String password = loginMap.get("password");
        try {
            //密码加密(参数说明： 1、加密内容 2、盐 3、散列次数)
            password = new Md5Hash(password,mobile,3).toString();
            System.out.println("password="+password);
            //1、构造登录令牌
            UsernamePasswordToken upToken = new UsernamePasswordToken(mobile,password);
            //2、获取subject
            Subject subject = SecurityUtils.getSubject();
            //3、调用login方法，进入realm完成认证
            subject.login(upToken);
            //4、获取sessionId
            String sessionId = (String) subject.getSession().getId();
            //5、构造返回结果
            return new Result(ResultCode.SUCCESS,sessionId);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return new Result(ResultCode.MOBILE_OR_PASSWORDERROR);
        }
        /*User user = userService.findByMobile(mobile);
        //登录失败
        if(user == null || !user.getPassword().equals(password)){
            return new Result(ResultCode.MOBILE_OR_PASSWORDERROR);
        }else {
            //登录成功
            //api权限字符串
            StringBuilder sb = new StringBuilder();
            //获取到所有可访问的角色
            for (Role role : user.getRoles()) {
                //获取到所有可访问的权限
                for (Permission perm : role.getPermissions()) {
                    if(perm.getType() == PermissionConstants.PY_API){
                        sb.append(perm.getCode()).append(",");
                    }
                }
            }
            Map<String,Object> map = new HashMap<>();
            //可访问的api权限字符串
            map.put("apis",sb.toString());
            map.put("companyId",user.getCompanyId());
            map.put("companyName",user.getCompanyName());
            String token = jwtUtils.createJwt(user.getId(), user.getUsername(), map);
            return new Result(ResultCode.SUCCESS,token);
        }*/
    }

    /**
     * 用户登录成功之后，获取用户信息
     * @return
     */
    @PostMapping("/profile")
    public Result profile(HttpServletRequest request) throws Exception {
        //构造subject，获取session中的安全数据
        Subject subject = SecurityUtils.getSubject();
        //2、根据subject获取所有的安全数据集合
        PrincipalCollection principals = subject.getPrincipals();
        //3、获取安全数据
        ProfileResult result = (ProfileResult) principals.getPrimaryPrincipal();
        /*String userId = claims.getId();
        //4、根据用户id查询用户
        User user = userService.findUserById(userId);
        //4.1、根据不同的用户级别获取用户权限
        ProfileResult result = null;
        if("user".equals(user.getLevel())){
            //4.2、企业用户具有的当前角色权限
            result = new ProfileResult(user);
        }else {
            Map map = new HashMap();
            if("coAdmin".equals(user.getLevel())){
                //4.3、企业管理员具有的所有的企业权限
                map.put("enVisible","1");
            }
            //4.4、saas平台管理员具有所有的权限
            List<Permission> list = permissionService.findAllPermissions(map);
            result = new ProfileResult(user,list);
        }*/
        //5、构建返回值对象
        return new Result(ResultCode.SUCCESS,result);
    }

    /**
     * 导入Excel，添加用户
     * @param file
     * @return
     */
    @PostMapping("/user/import")
    public Result importUser(@RequestParam(name = "file")MultipartFile file) throws Exception {
        //1、解析Excel
        //1.1.根据Excel文件创建工作簿
        Workbook wb = new XSSFWorkbook(file.getInputStream());
        //1.2.获取Sheet
        Sheet sheet = wb.getSheetAt(0);//参数：索引
        //1.3.获取Sheet中的每一行，和每一个单元格
        //2、获取用户数据列表
        List<User> userList = new ArrayList<>();
        for (int rowNum = 1; rowNum<= sheet.getLastRowNum() ;rowNum ++) {
            Row row = sheet.getRow(rowNum);//根据索引获取每一个行
            Object [] values = new Object[row.getLastCellNum()];
            for(int cellNum = 1;cellNum< row.getLastCellNum(); cellNum ++) {
                //根据索引获取每一个单元格
                Cell cell = row.getCell(cellNum);
                //获取每一个单元格的内容
                Object value = getCellValue(cell);
                values[cellNum] = value;
            }
            User user = new User(values);
            userList.add(user);
        }

        //3、批量保存用户
        userService.saveAll(userList,companyId,companyName);
        return new Result(ResultCode.SUCCESS);
    }

    public static Object getCellValue(Cell cell) {
        //1.获取到单元格的属性类型
        CellType cellType = cell.getCellType();
        //2.根据单元格数据类型获取数据
        Object value = null;
        switch (cellType) {
            case STRING:
                value = cell.getStringCellValue();
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case NUMERIC:
                if(DateUtil.isCellDateFormatted(cell)) {
                    //日期格式
                    value = cell.getDateCellValue();
                }else{
                    //数字
                    value = cell.getNumericCellValue();
                }
                break;
            case FORMULA: //公式
                value = cell.getCellFormula();
                break;
            default:
                break;
        }
        return value;
    }

}
