/**
 * Copyright 2013 Nils Assbeck, Guersel Ayaz and Michael Zoech
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tumao.sync.ptp.commands.nikon;


import com.tumao.sync.ptp.NikonCamera;
import com.tumao.sync.ptp.PtpAction;
import com.tumao.sync.ptp.PtpCamera;
import com.tumao.sync.ptp.PtpConstants;
import com.tumao.sync.ptp.commands.SimpleCommand;

public class NikonStartLiveViewAction implements PtpAction {

    private final NikonCamera camera;

    public NikonStartLiveViewAction(NikonCamera camera) {
        this.camera = camera;
    }

    @Override
    public void exec(PtpCamera.IO io) {
        SimpleCommand simpleCmd = new SimpleCommand(camera, PtpConstants.Operation.NikonStartLiveView);
        io.handleCommand(simpleCmd);

        if (simpleCmd.getResponseCode() != PtpConstants.Response.Ok) {
            return;
        }

        SimpleCommand deviceReady = new SimpleCommand(camera, PtpConstants.Operation.NikonDeviceReady);
        for (int i = 0; i < 10; ++i) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                // nop
            }

            deviceReady.reset();
            io.handleCommand(deviceReady);
            if (deviceReady.getResponseCode() == PtpConstants.Response.DeviceBusy) {
                // still waiting
            } else if (deviceReady.getResponseCode() == PtpConstants.Response.Ok) {
                camera.onLiveViewStarted();
                return;
            } else {
                return;
            }
        }
    }

    @Override
    public void reset() {
    }
}
