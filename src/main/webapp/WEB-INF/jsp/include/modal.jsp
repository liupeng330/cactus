<%@ page pageEncoding="UTF-8" %>
<script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/modalService-8-20.js"></script>
<div id="warningModal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4  class="modal-title" style="text-align: left">确认操作</h4>
            </div>
            <div class="modal-body">
                <p id="confirmInfo" style="text-align: left;">是否确定？</p>
                <div id="submitResult" class="wrapTd" style="color: red;text-align: left">

                </div>
            </div>
            <div class="modal-footer">
                <button id="no" type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button id="yes" type="button" class="btn btn-primary">确定</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<div id="info-modal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 id="info-modal-title" class="modal-title">确认操作</h4>
            </div>
            <div id="info-modal-body" class="modal-body">

            </div>
            <div class="modal-footer">
                <button id="info-modal-no" type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button id="info-modal-yes" type="button" class="btn btn-primary">确定</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->