SUMMARY = "The Microsoft Build Engine is a platform for building applications."
HOMEPAGE = "https://github.com/mono/msbuild"
SECTION = "console/apps"
LICENSE = "MIT"
DEPENDS = " \
			coreutils-native findutils-native curl-native \
			unzip-native mono-native make-native \
		"

LIC_FILES_CHKSUM = "file://LICENSE;md5=768c552053144071f8ef7e5621714b0a"

inherit mono

#SRCBRANCH = "update-msbuild-16"
#SRCREV = "883603959ff53aac1bb6c0d1155a45ff1a3e4d6e"
SRCBRANCH = "xplat-master"
SRCREV = "8b3a694c2c419582108588a7bd1133630a1b8077"

SRC_URI = " \
			git://github.com/mono/msbuild.git;protocol=https;branch=${SRCBRANCH} \
		"

#SRC_URI += " file://msbuild-16.0-remove-om.unittests.patch "

S = "${WORKDIR}/git"

MSBUILDEXTRAOPTS = ""
#MSBUILDEXTRAOPTS += " /p:DisableNerdbankVersioning=true "
#MSBUILDEXTRAOPTS += " /p:AutoGenerateBindingRedirects=false "

do_compile () {
	./eng/cibuild_bootstrapped_msbuild.sh --host_type mono --configuration Release --binaryLog --skip_tests ${MSBUILDEXTRAOPTS}
}

do_install () {
	./install-mono-prefix.sh ${D}${prefix}
}

do_install_append_class-target() {
	#install -d -m0755 ${D}${libdir}/mono/msbuild/15.0/bin/Roslyn
	#install -m0755 ${S}/bin/Release-MONO/AnyCPU/Unix/Unix_Deployment/Roslyn/* ${D}${libdir}/mono/msbuild/15.0/bin/Roslyn/
}

FILES_${PN} += " \
			${libdir}/mono/ \
		"

SYSROOT_PREPROCESS_FUNCS += "msbuild_sysroot_preprocess"

msbuild_sysroot_preprocess () {
	install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}

	install -m0755 ${S}/msbuild-mono-deploy.in ${SYSROOT_DESTDIR}${bindir_crossscripts}/msbuild
	sed -i -es,@bindir@,${STAGING_BINDIR_NATIVE}, ${SYSROOT_DESTDIR}${bindir_crossscripts}/msbuild
	sed -i -es,@mono_instdir@,${STAGING_LIBDIR}/mono, ${SYSROOT_DESTDIR}${bindir_crossscripts}/msbuild
}

BBCLASSEXTEND = "native"
