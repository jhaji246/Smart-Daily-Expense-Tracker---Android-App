#!/bin/bash

# 🚀 CI/CD Pipeline Local Testing Script
# This script helps test CI/CD pipeline components locally before pushing to GitHub

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    if ! command_exists java; then
        print_error "Java is not installed. Please install JDK 17 or later."
        exit 1
    fi
    
    if ! command_exists ./gradlew; then
        print_error "Gradle wrapper not found. Please run from project root."
        exit 1
    fi
    
    if ! command_exists git; then
        print_error "Git is not installed."
        exit 1
    fi
    
    print_success "All prerequisites are met!"
}

# Function to run unit tests
run_unit_tests() {
    print_status "Running unit tests..."
    
    if ./gradlew testDebugUnitTest --console=plain; then
        print_success "Unit tests passed!"
    else
        print_error "Unit tests failed!"
        exit 1
    fi
}

# Function to run linting
run_lint() {
    print_status "Running linting..."
    
    if ./gradlew lintDebug --console=plain; then
        print_success "Linting passed!"
    else
        print_warning "Linting found issues. Check reports in app/build/reports/lint/"
    fi
}

# Function to run Detekt analysis
run_detekt() {
    print_status "Running Detekt analysis..."
    
    if ./gradlew detekt --console=plain; then
        print_success "Detekt analysis passed!"
    else
        print_warning "Detekt found issues. Check reports in app/build/reports/detekt/"
    fi
}

# Function to generate code coverage
generate_coverage() {
    print_status "Generating code coverage report..."
    
    if ./gradlew jacocoTestReport --console=plain; then
        print_success "Code coverage report generated!"
        print_status "Coverage report available at: app/build/reports/jacoco/jacocoTestReport/html/index.html"
    else
        print_error "Failed to generate code coverage report!"
        exit 1
    fi
}

# Function to check SonarQube configuration
check_sonarqube_config() {
    print_status "Checking SonarQube configuration..."
    
    if [ -f "sonar-project.properties" ]; then
        print_success "SonarQube project configuration found"
    else
        print_error "SonarQube project configuration not found"
        exit 1
    fi
    
    if [ -f "detekt-config.yml" ]; then
        print_success "Detekt configuration found"
    else
        print_error "Detekt configuration not found"
        exit 1
    fi
    
    if [ -f "quality-gate.yml" ]; then
        print_success "Quality gate configuration found"
    else
        print_error "Quality gate configuration not found"
        exit 1
    fi
    
    print_success "All SonarQube configuration files are present!"
}

# Function to build APKs
build_apks() {
    print_status "Building APKs..."
    
    # Build debug APK
    if ./gradlew assembleDebug --console=plain; then
        print_success "Debug APK built successfully!"
    else
        print_error "Debug APK build failed!"
        exit 1
    fi
    
    # Build release APK
    if ./gradlew assembleRelease --console=plain; then
        print_success "Release APK built successfully!"
    else
        print_warning "Release APK build failed (this is expected without signing config)"
    fi
}

# Function to check code quality
check_code_quality() {
    print_status "Checking code quality..."
    
    # Check for TODO comments
    TODO_COUNT=$(grep -r "TODO" app/src/main/java/ --include="*.kt" | wc -l)
    if [ "$TODO_COUNT" -gt 0 ]; then
        print_warning "Found $TODO_COUNT TODO comments in code"
        grep -r "TODO" app/src/main/java/ --include="*.kt" | head -5
    else
        print_success "No TODO comments found!"
    fi
    
    # Check for FIXME comments
    FIXME_COUNT=$(grep -r "FIXME" app/src/main/java/ --include="*.kt" | wc -l)
    if [ "$FIXME_COUNT" -gt 0 ]; then
        print_warning "Found $FIXME_COUNT FIXME comments in code"
        grep -r "FIXME" app/src/main/java/ --include="*.kt" | head -5
    else
        print_success "No FIXME comments found!"
    fi
}

# Function to check dependencies
check_dependencies() {
    print_status "Checking dependencies..."
    
    # Check for outdated dependencies
    if ./gradlew dependencyUpdates --console=plain; then
        print_success "Dependency check completed!"
    else
        print_warning "Dependency check failed or found updates"
    fi
}

# Function to validate CI/CD files
validate_cicd_files() {
    print_status "Validating CI/CD configuration files..."
    
    # Check if workflow files exist
    if [ -f ".github/workflows/android-ci.yml" ]; then
        print_success "Main CI/CD workflow found"
    else
        print_error "Main CI/CD workflow not found"
        exit 1
    fi
    
    if [ -f ".github/workflows/quick-ci.yml" ]; then
        print_success "Quick CI workflow found"
    else
        print_error "Quick CI workflow not found"
        exit 1
    fi
    
    if [ -f ".github/workflows/release.yml" ]; then
        print_success "Release workflow found"
    else
        print_error "Release workflow not found"
        exit 1
    fi
    
    if [ -f ".github/workflows/dependency-update.yml" ]; then
        print_success "Dependency update workflow found"
    else
        print_error "Dependency update workflow not found"
        exit 1
    fi
    
    print_success "All CI/CD configuration files are present!"
}

# Function to check git status
check_git_status() {
    print_status "Checking git status..."
    
    # Check if we're on a clean branch
    if [ -n "$(git status --porcelain)" ]; then
        print_warning "Working directory is not clean. Uncommitted changes detected:"
        git status --short
    else
        print_success "Working directory is clean!"
    fi
    
    # Check current branch
    CURRENT_BRANCH=$(git branch --show-current)
    print_status "Current branch: $CURRENT_BRANCH"
    
    # Check if we're up to date with remote
    if git fetch origin >/dev/null 2>&1; then
        LOCAL_COMMIT=$(git rev-parse HEAD)
        REMOTE_COMMIT=$(git rev-parse origin/$CURRENT_BRANCH 2>/dev/null || echo "N/A")
        
        if [ "$LOCAL_COMMIT" = "$REMOTE_COMMIT" ]; then
            print_success "Branch is up to date with remote"
        else
            print_warning "Branch is not up to date with remote"
        fi
    else
        print_warning "Could not fetch from remote"
    fi
}

# Function to simulate CI/CD pipeline
simulate_cicd() {
    print_status "Simulating CI/CD pipeline locally..."
    
    echo "=========================================="
    echo "🚀 CI/CD Pipeline Simulation"
    echo "=========================================="
    
    # Simulate build stage
    echo "📱 Stage 1: Build"
    build_apks
    
    # Simulate test stage
    echo "🧪 Stage 2: Test"
    run_unit_tests
    
    # Simulate quality stage
    echo "🔍 Stage 3: Quality"
    run_lint
    check_code_quality
    
    # Simulate security stage
    echo "🔒 Stage 4: Security"
    check_dependencies
    
    echo "=========================================="
    echo "✅ CI/CD Pipeline Simulation Complete!"
    echo "=========================================="
}

# Function to show help
show_help() {
    echo "Usage: $0 [OPTION]"
    echo ""
    echo "Options:"
    echo "  --all              Run all checks (default)"
    echo "  --tests            Run only unit tests"
    echo "  --build            Run only build process"
    echo "  --quality          Run only quality checks"
echo "  --sonarqube        Check SonarQube configuration"
echo "  --detekt           Run Detekt analysis"
echo "  --coverage         Generate code coverage report"
    echo "  --cicd             Validate CI/CD configuration"
    echo "  --git              Check git status"
    echo "  --simulate         Simulate full CI/CD pipeline"
    echo "  --help             Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                 # Run all checks"
    echo "  $0 --tests         # Run only tests"
    echo "  $0 --simulate      # Simulate CI/CD pipeline"
}

# Main execution
main() {
    echo "🚀 Smart Daily Expense Tracker - CI/CD Local Testing"
    echo "=================================================="
    
    # Parse command line arguments
    case "${1:---all}" in
        --all)
            check_prerequisites
            validate_cicd_files
            check_sonarqube_config
            check_git_status
            run_unit_tests
            run_lint
            run_detekt
            generate_coverage
            build_apks
            check_code_quality
            check_dependencies
            ;;
        --tests)
            check_prerequisites
            run_unit_tests
            ;;
        --build)
            check_prerequisites
            build_apks
            ;;
        --quality)
            check_prerequisites
            run_lint
            run_detekt
            generate_coverage
            check_code_quality
            ;;
        --cicd)
            validate_cicd_files
            ;;
        --sonarqube)
            check_sonarqube_config
            ;;
        --detekt)
            check_prerequisites
            run_detekt
            ;;
        --coverage)
            check_prerequisites
            generate_coverage
            ;;
        --git)
            check_git_status
            ;;
        --simulate)
            check_prerequisites
            simulate_cicd
            ;;
        --help)
            show_help
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
    
    echo ""
    print_success "All requested operations completed successfully!"
}

# Run main function with all arguments
main "$@"
