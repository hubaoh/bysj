package cn.edu.asu.bysj.modules.sys.control;

import cn.edu.asu.bysj.common.annotation.Log;
import cn.edu.asu.bysj.common.control.BaseController;
import cn.edu.asu.bysj.common.domain.ResponseBo;
import cn.edu.asu.bysj.common.util.MD5Utils;
import cn.edu.asu.bysj.modules.sys.entity.User;
import cn.edu.asu.bysj.modules.sys.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController extends BaseController{
    @Autowired
   private UserService userService;

    @RequestMapping(path = "user")
    public String index(Model model) {
        User user = super.getCurrentUser();
        model.addAttribute("user", user);
        return "system/user/user";
    }

    @RequestMapping(path = "user/checkUserName")
    @ResponseBody
    public boolean checkUserName(String username, String oldusername) {
        if (StringUtils.isNotBlank(oldusername) && username.equalsIgnoreCase(oldusername)) {
            return true;
        }
        User result=userService.findByName(username);
        if(result!=null){
            return false;
        }
        return true;
    }

    @RequestMapping(path = "user/getUser")
    @ResponseBody
    public ResponseBo getUser(Long userId) {
        try {
           User user =userService.findById(userId);
            return ResponseBo.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("获取用户信息失败，请联系网站管理员！");
        }
    }

//    @Log("获取用户信息")
//    @RequestMapping(path = "user/list")
//    @ResponseBody
//    public Map<String, Object> userList(QueryRequest request, User user) {
//        PageHelper.startPage(request.getPageNum(), request.getPageSize());
//        List<User> list = this.userService.findUserWithDept(user);
//        PageInfo<User> pageInfo = new PageInfo<User>(list);
//        return getDataTable(pageInfo);
//    }

    @RequestMapping(path = "user/regist")
    @ResponseBody
    public ResponseBo regist(User user) {
        try {
            User result = this.userService.findByName(user.getUsername());
            if (result != null) {
                return ResponseBo.warn("该用户名已被使用！");
            }
            this.userService.registUser(user);
            return ResponseBo.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("注册失败，请联系网站管理员！");
        }
    }
    /**
     * 更换用户主题
     * @param theme
     * @param username
     * @return
     */
    @Log("更换主题")
    @RequestMapping(path = "user/theme")
    @ResponseBody
    public ResponseBo updateTheme(@RequestParam  String theme, @RequestParam  String  username) {
        try {
            this.userService.updateTheme(theme,username);
            return ResponseBo.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error();
        }
     }

    /**
     * 检查密码是否相等
     * @param password
     * @return
     */
    @RequestMapping(path = "user/checkPassword")
    @ResponseBody
    public boolean checkPassword(String password) {
        User user = super.getCurrentUser();
        String encrypt = MD5Utils.encrypt(user.getUsername().toLowerCase(), password);
        if (user.getPassword().equals(encrypt)){
            return true;
        }
        return false;
    }

    /**
     * 更新用户密码
     * @param newPassword
     * @return
     */
    @RequestMapping("user/updatePassword")
    @ResponseBody
    public ResponseBo updatePassword(String newPassword) {
        try {
            this.userService.updatePassword(newPassword);
            return ResponseBo.ok("更改密码成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("更改密码失败，请联系网站管理员！");
        }
    }
   //获取用户个人信息
    @RequestMapping(path = "user/profile")
    public String profileIndex(Model model) {
        User user = super.getCurrentUser();
        user = this.userService.findUserProfile(user);
         String  ssex = user.getUser_sex();
        if ("0".equals(ssex)) {
            user.setUser_sex("性别：男");
        } else if ("1".equals(ssex)) {
            user.setUser_sex("性别：女");
        } else {
            user.setUser_sex("性别：保密");
        }
        model.addAttribute("user", user);
        return "system/user/profile";
    }

    //@RequestParam(name = "user_id")

    /**
     * 获取个人信息
     * @param user_id
     * @return
     */
    @RequestMapping(path = "user/getUserProfile")
    @ResponseBody
    public ResponseBo getUserProfile(@RequestParam Long user_id) {
        try {
            User user = new User();
            user.setUser_id(user_id);
            return ResponseBo.ok(this.userService.findUserProfile(user));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("获取用户信息失败，请联系网站管理员！");
        }
    }

    @RequestMapping(path = "user/updateUserProfile")
    @ResponseBody
    public ResponseBo updateUserProfile(User user) {
        try {
            this.userService.updateUserProfile(user);
            return ResponseBo.ok("更新个人信息成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("获取用户信息失败，请联系网站管理员！");
        }
    }


    //更换用户头像
   @RequestMapping(path = "user/changeAvatar")
   @ResponseBody
   public ResponseBo changeAvatar(@RequestParam  String imgName) {
       try {
           String[] img = imgName.split("/");
           String realImgName = img[img.length-1];
           User user = super.getCurrentUser();
           user.setUser_photo_url(realImgName);
           this.userService.savaUser(user);
           return ResponseBo.ok("更新头像成功！");
       } catch (Exception e) {
           e.printStackTrace();
           return ResponseBo.error("更新头像失败，请联系网站管理员！");
       }
   }
}
