package com.rui.material_design.dynamic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.soyute.comresource.widget.HeadBar;
import com.soyute.comservice.constant.CircleValue;
import com.soyute.comservice.model.UserInfo;
import com.soyute.comservice.model.circle.datamodel.TagModel;
import com.soyute.mvp.base.BaseActivity;
import com.soyute.supplychain.circle.R;
import com.soyute.supplychain.circle.R2;
import com.soyute.supplychain.circle.contract.DynamicPublishView;
import com.soyute.supplychain.circle.di.InformationComponentFactory;
import com.soyute.supplychain.circle.di.component.DynamicPublishComponent;
import com.soyute.supplychain.circle.imgselector_custom.PictureSelector3;
import com.soyute.supplychain.circle.model.parammodel.PublicModel;
import com.soyute.supplychain.circle.presenter.DynamicPublishPresenter;
import com.soyute.supplychain.circle.ui.adapter.DynamicPublishAdapter;
import com.soyute.supplychain.circle.ui.adapter.DynamicTagAdapter;
import com.soyute.supplychain.circle.viewstate.DynamicPublishActivityViewState;
import com.soyute.toolkit.utils.DisplayUtils;
import com.soyute.viewkit.layoutmanager.FullyGridLayoutManager;
import com.soyute.viewkit.widget.toast.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 *
 */
public class DynamicPublishActivity extends BaseActivity<DynamicPublishComponent
        , DynamicPublishView, DynamicPublishPresenter
        , DynamicPublishActivityViewState>
        implements DynamicPublishView, ViewTreeObserver.OnGlobalLayoutListener {

    @BindView(R2.id.et_input_content)
    EditText etInputContent;
    @BindView(R2.id.rv_add_pic)
    RecyclerView rvAddPic;
    @BindView(R2.id.rv_add_tag)
    RecyclerView rvAddTag;
    @BindView(R2.id.tv_add_tag)
    TextView tvAddTag;
    @BindView(R2.id.tv_tag_hint)
    TextView tvTagHint;
    @BindView(R2.id.ll_add_tag)
    LinearLayout llAddTag;
    @BindView(R2.id.tv_publish_scope)
    TextView tvPublishScope;
    @BindView(R2.id.rl_publish_scope)
    RelativeLayout rlPublishScope;
    @BindView(R2.id.head_bar)
    HeadBar headBar;


    private DynamicPublishAdapter adapter;
    private List<LocalMedia> selectList;
    private int maxSelectNum = 9;

    private List<TagModel> selectTags;
    private DynamicTagAdapter selectAdapter;
    private PublicModel publicModel;
    private int screenWidth;
    private int rvWidth;
    private int tvWidth;
    private int rvMaxWith;
    private String type = CircleValue.INFO_TYPE_TXT;
    private boolean isPublic = true;
    private String memberIds;
    private String groupIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTagView();
        initViews();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dynamic_publish;
    }

    private void initTagView() {
        screenWidth = DisplayUtils.getScreenWidthAndHight(this)[0];
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        linearLayoutManager.setStackFromEnd(true);
        rvAddTag.setLayoutManager(linearLayoutManager);
        selectTags = new ArrayList<>();
        selectAdapter = new DynamicTagAdapter(R.layout.item_dynamic_select_tag, selectTags);
        rvAddTag.setAdapter(selectAdapter);
    }

    private void initViews() {
        selectList = new ArrayList<>();
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        rvAddPic.setLayoutManager(manager);
        adapter = new DynamicPublishAdapter(this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        rvAddPic.setAdapter(adapter);
        adapter.setOnItemClickListener((position, v) -> {
            if (selectList.size() > 0) {
                LocalMedia media = selectList.get(position);
                String pictureType = media.getPictureType();
                int mediaType = PictureMimeType.pictureToVideo(pictureType);
                switch (mediaType) {
                    case 1:
                        // 预览图片 可自定长按保存路径
                        //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
                        PictureSelector3.create(DynamicPublishActivity.this)
                                .externalPicturePreview(position, selectList, CircleValue.RESULT_CODE_PREVIEW_IMG);
                        break;
                    case 2:
                        // 预览视频
                        PictureSelector3.create(DynamicPublishActivity.this).externalPictureVideo(media.getPath());
                        break;
                    case 3:
                        // 预览音频
//                        PictureSelector2.create(DynamicPublishActivity.this).externalPictureAudio(media.getPath());
                        break;
                }
            }
        });

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    PictureFileUtils.deleteCacheDirFile(DynamicPublishActivity.this);
                } else {
                    Toast.makeText(DynamicPublishActivity.this,
                            getString(R.string.picture_jurisdiction), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });

        headBar.SetBtnRightOnclickerListener(v -> commitPublish());
    }

    private void commitPublish() {
        String content = etInputContent.getText().toString().trim();
        List<TagModel> data = selectAdapter.getData();
        String tagIds = getTagString(data, false);
        String tagName = getTagString(data, true);
        if (publicModel != null) {
            isPublic = publicModel.isPublic();
            if (!isPublic) {
                memberIds = publicModel.getFriendId();
                groupIds = publicModel.getGroupId();
            }
        }
        if (TextUtils.isEmpty(content) && (selectList == null || selectList.size() == 0)) {
            ToastUtils.showToast(this, "请填入动态内容后发布！");
            return;
        }
        getPresenter().publishDynamic(content, selectList, tagIds, tagName, memberIds
                , groupIds, isPublic, type, UserInfo.getToken());
    }


    private String getTagString(List<TagModel> tags, boolean isCustom) {
        if (tags == null || tags.size() == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        if (!isCustom) {
            for (TagModel tag : tags) {
                if (!TextUtils.isEmpty(builder)) {
                    builder.append(",");
                }
                if (tag.getId() >= 0) builder.append(tag.getId());
            }
        } else {
            for (TagModel tag : tags) {
                if (!TextUtils.isEmpty(builder)) {
                    builder.append(",");
                }
                if (tag.getId() < 0) builder.append(tag.getTagName());
            }
        }
        return TextUtils.isEmpty(builder) ? null : builder.toString();
    }


    @Override
    public void showLoadingFailureError() {
        ToastUtils.showNetWorkErro();
    }

    @Override
    protected DynamicPublishComponent constructComponent() {
        return InformationComponentFactory.getDynamicPublishComponent();
    }

    @OnClick({R2.id.tv_publish_scope, R2.id.rl_publish_scope, R2.id.ll_add_tag})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.tv_publish_scope) {
            DynamicScopeActivity.actionStart(this, CircleValue.RESULT_CODE_ADD_SCOPE, publicModel);
        } else if (id == R.id.rl_publish_scope) {
            DynamicScopeActivity.actionStart(this, CircleValue.RESULT_CODE_ADD_SCOPE, publicModel);
        } else if (id == R.id.ll_add_tag) {
            DynamicAddTagActivity.actionStart(this, selectAdapter.getData(), CircleValue.RESULT_CODE_ADD_TAG);
        }
    }

    private DynamicPublishAdapter.OnAddPicClickListener onAddPicClickListener = () -> {
        // 进入相册 以下是例子：不需要的api可以不写
        PictureSelector3.create(DynamicPublishActivity.this)
                .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles
                .maxSelectNum(maxSelectNum)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .previewVideo(true)// 是否可预览视频
                .enablePreviewAudio(true)// 是否预览音频
//                .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                //.setOutputCameraPath("/Chinayie/App")// 自定义拍照保存路径
                .compress(false)// 是否压缩
//                .compressMode(PictureConfig.SYSTEM_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .isGif(true)// 是否显示gif图片
                .openClickSound(false)// 是否开启点击声音
                .selectionMedia(selectList)// 是否传入已选图片
                //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.compressMaxKB()//压缩最大值kb compressGrade()为Luban.CUSTOM_GEAR有效
                //.compressWH() // 压缩宽高比 compressGrade()为Luban.CUSTOM_GEAR有效
                //.videoQuality()// 视频录制质量 0 or 1
                //.videoSecond()//显示多少秒以内的视频
                //.recordVideoSecond()//录制视频秒数 默认60秒
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST: {
                    // 图片选择
                    selectList = PictureSelector.obtainMultipleResult(data);
                    int pictureType = PictureMimeType.isPictureType(selectList.get(0).getPictureType());
                    if (pictureType != PictureConfig.TYPE_IMAGE) {
                        type = CircleValue.INFO_TYPE_VDO;
                        adapter.setSelectMax(1);
                    } else {
                        type = CircleValue.INFO_TYPE_IMG;
                    }
                    // 例如 LocalMedia 里面返回两种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    Timber.d("-------->PictureConfig.onActivityResult:" + selectList.size());
                    break;
                }
                case CircleValue.RESULT_CODE_PREVIEW_IMG: {
                    // 图片预览选择
                    selectList = (List<LocalMedia>) data.getSerializableExtra("selectList");
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    Timber.d("-------->onActivityResult:selectList=" + selectList.size());
                    break;
                }
                case CircleValue.RESULT_CODE_ADD_TAG: {//添加标签
                    handerTags(data);
                    break;
                }
                case CircleValue.RESULT_CODE_ADD_SCOPE: {
                    publicModel = (PublicModel) data.getSerializableExtra("publicModel");
                    if (publicModel.isPublic()) {
                        tvPublishScope.setText("公开");
                    } else {
                        tvPublishScope.setText(publicModel.getScopeStr());
                    }
                    break;
                }
            }
        }
    }

    private void handerTags(Intent data) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rvAddTag.getLayoutParams();
        params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        rvAddTag.setLayoutParams(params);
        initTreeObserver();
        selectAdapter.setNewData((List<TagModel>) data.getSerializableExtra("selectTags"));
        rvAddTag.scrollToPosition(selectAdapter.getItemCount() - 1);
        if (selectAdapter.getItemCount() != 0) {
            tvTagHint.setVisibility(View.GONE);
        } else {
            tvTagHint.setVisibility(View.VISIBLE);
        }
        Timber.d("-------->onActivityResult:selectAdapter.getItemCount()=" + selectAdapter.getItemCount());
    }

    /**
     * 注册监听,在一个视图树中的焦点状态发生改变时，注册回调接口来获取view的尺寸
     */
    private void initTreeObserver() {
        ViewTreeObserver vto = rvAddTag.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(this);
        ViewTreeObserver vto2 = tvAddTag.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(this);
    }

    @SuppressLint("NewApi")
    @Override
    public void onGlobalLayout() {
        tvAddTag.getViewTreeObserver().removeOnGlobalLayoutListener(this);//删除监听
        rvAddTag.getViewTreeObserver().removeOnGlobalLayoutListener(this);//删除监听
        rvWidth = rvAddTag.getWidth();
        tvWidth = tvAddTag.getWidth();
        rvMaxWith = screenWidth - tvWidth;
        if (rvWidth > rvMaxWith) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rvAddTag.getLayoutParams();
            params.width = rvMaxWith;
            rvAddTag.setLayoutParams(params);
        }
    }

    @Override
    public void onPublishDynamic() {
        //通知主页刷新数据
        ToastUtils.showToast(this, "发布动态成功！");
        finish();
    }

    /**
     * 开发测试用,非正式项目
     */
    @Override
    public void showDialog() {
        String message = "发布中";
        Message message1 = new Message();
        message1.what = 1;
        message1.obj = TextUtils.isEmpty(message) ? "" : message;
        mProcessDialogHandler.sendMessage(message1);
    }

}
